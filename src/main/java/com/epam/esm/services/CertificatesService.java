package com.epam.esm.services;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.dao.exceptions.EntityNotFoundException;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.CertificateDTO;
import com.epam.esm.models.dtos.TagDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Myroslav Dudnyk
 */
@Service
public class CertificatesService {
    private final CertificateDAO certificateDAO;
    private final TagsService tagsService;
    private final CertificatesTagsService certificatesTagsService;

    public CertificatesService(final CertificateDAO certificateDAO,
                               final TagsService tagsService,
                               final CertificatesTagsService certificatesTagsService) {
        this.certificateDAO = certificateDAO;
        this.tagsService = tagsService;
        this.certificatesTagsService = certificatesTagsService;
    }

    public List<Certificate> getCertificates(String tagName, String searchText, String sortBy, String sortOrder) {
        // If tag name is specified, get certificates by tag name otherwise get all certificates
        List<Certificate> certificates = getCertificatesByTagNameOrGetAllCertificates(tagName);

        // If search text is specified, filter certificates by part of name or description
        certificates = filterCertificatesBySearchText(certificates, searchText);

        // Sort certificates by date or name with specified sort order
        sortCertificates(certificates, sortBy, sortOrder);

        // Populate tags list for every certificate from certificates list
        populateCertificateTags(certificates);

        return certificates;
    }

    private List<Certificate> getCertificatesByTagNameOrGetAllCertificates(String tagName) {
        if (tagName != null && !tagName.isEmpty()) {
            return certificateDAO.getCertificatesByTagName(tagName);
        } else {
            return certificateDAO.getAll();
        }
    }

    private List<Certificate> filterCertificatesBySearchText(List<Certificate> certificates, String searchText) {
        if (searchText != null && !searchText.isEmpty()) {
            return certificates.stream()
                    .filter(cert -> cert.getName().contains(searchText) || cert.getDescription().contains(searchText))
                    .collect(Collectors.toList());
        } else {
            return certificates;
        }
    }

    private void sortCertificates(List<Certificate> certificates, String sortBy, String sortOrder) {
        if (sortBy != null && !sortBy.isEmpty()) {
            if (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) {
                if (sortBy.equalsIgnoreCase("date")) {
                    certificates.sort(Comparator.comparing(Certificate::getCreateDate).reversed());
                } else if (sortBy.equalsIgnoreCase("name")) {
                    certificates.sort(Comparator.comparing(Certificate::getName).reversed());
                }
            } else {
                if (sortBy.equalsIgnoreCase("date")) {
                    certificates.sort(Comparator.comparing(Certificate::getCreateDate));
                } else if (sortBy.equalsIgnoreCase("name")) {
                    certificates.sort(Comparator.comparing(Certificate::getName));
                }
            }
        }
    }

    private void populateCertificateTags(List<Certificate> certificates) {
        for (Certificate certificate : certificates) {
            certificate.setTags(tagsService.getTagsByCertificateId(certificate.getId()));
        }
    }

    public Certificate getById(int id) {
        Certificate certificate = certificateDAO.getById(id);
        certificate.setTags(tagsService.getTagsByCertificateId(id));

        return certificate;
    }

    @Transactional
    public Certificate create(CertificateDTO certificate) {
        int newCertificateId = certificateDAO.create(certificate);

        for (String tagName : certificate.getTags()) {
            int tagId = addTagToDBIfNotPresented(tagName);
            certificatesTagsService.create(newCertificateId, tagId);
        }

        return getById(newCertificateId);
    }

    @Transactional
    public Certificate update(int certificateId, CertificateDTO certificateNewEntity) {
        // Retrieve the old certificate entity from the database
        Certificate certificateOldEntity = getById(certificateId);

        // Update the certificate tags if present in the new entity
        if (certificateNewEntity.getTags() != null) {
            updateCertificateTags(certificateNewEntity, certificateOldEntity);
        }

        // Update the fields of the new entity with values from the old entity, if they are null
        int fieldsToUpdateCount =
                fillNullFieldsOfNewEntityWithFieldsFromOldEntity(certificateNewEntity, certificateOldEntity);

        // If any fields were updated, update the certificate entity in the database
        if (fieldsToUpdateCount > 0) {
            certificateDAO.update(certificateId, certificateNewEntity);
        }

        // Return the updated certificate entity from the database
        return getById(certificateId);
    }

    // Fills null fields of certificateNewEntity with values from certificateOldEntity
    // and returns the number of updated fields
    private int fillNullFieldsOfNewEntityWithFieldsFromOldEntity(final CertificateDTO certificateNew,
                                                                 final Certificate certificateOld) {
        // Counter to check how many fields were updated
        int updatedFieldCount = 0;

        // Update the name field
        updatedFieldCount += updateField(certificateNew::getName, certificateOld::getName, certificateNew::setName);

        // Update the description field
        updatedFieldCount += updateField(certificateNew::getDescription, certificateOld::getDescription, certificateNew::setDescription);

        // Update the price field
        updatedFieldCount += updateField(certificateNew::getPrice, certificateOld::getPrice, certificateNew::setPrice);

        // Update the duration field
        updatedFieldCount += updateField(certificateNew::getDuration, certificateOld::getDuration, certificateNew::setDuration);

        return updatedFieldCount;
    }

    // Helper method that updates a field of a certificate entity
    private <T> int updateField(Supplier<T> newValueGetter, Supplier<T> oldValueGetter, Consumer<T> valueSetter) {
        T newValue = newValueGetter.get();
        T oldValue = oldValueGetter.get();

        if (newValue != null && !newValue.equals(oldValue)) {
            valueSetter.accept(newValue);
            return 1;
        } else if (newValue == null) {
            valueSetter.accept(oldValue);
            return 1;
        }

        return 0;
    }

    // Updates the tags of a certificate based on the passed in CertificateDTO and existing Certificate
    private void updateCertificateTags(final CertificateDTO certificateNewEntity, final Certificate certificateOldEntity) {
        // Get the list of tags from the old certificate entity
        List<Tag> tagListFromOldEntity = certificateOldEntity.getTags();

        // Get the list of tag names from the new certificate DTO
        List<String> tagNameListFromNewEntity = certificateNewEntity.getTags();

        // Get the list of tag names from the old certificate entity
        List<String> tagNameListFromOldEntity = tagListFromOldEntity.stream().map(Tag::getName).toList();

        // Get the ID of the certificate being updated
        int certificateId = certificateOldEntity.getId();


        // Iterate over each tag name in the list of tag names from the new certificate entity
        for (String tagName : tagNameListFromNewEntity) {
            // If the tag name is not in the list of tag names from the old certificate entity
            if (isTagNameNotInTagNameList(tagName, tagNameListFromOldEntity)) {
                // Add the new tag to the database (only if it doesn't exist in the database) and return its ID
                int tagId = addTagToDBIfNotPresented(tagName);
                // Add the tag to the certificate entity
                certificatesTagsService.create(certificateId, tagId);
            }
        }

        // Iterate over each tag in the list of tags from the old certificate entity
        for (Tag tag : tagListFromOldEntity) {
            // If the tag is not in the list of tag names from the new certificate entity
            if (isTagNameNotInTagNameList(tag.getName(), tagNameListFromNewEntity)) {
                // Remove the tag from the certificate entity
                certificatesTagsService
                        .deleteByCertificateIdAndTagId(certificateId, tag.getId());
            }
        }
    }

    // Method checks if the tag name does not exist in the list of tag names.
    // If so, 'true' is returned.
    private boolean isTagNameNotInTagNameList(final String checkedTagName, final List<String> tagNameList) {
        return tagNameList.stream().filter(tagName -> tagName.equalsIgnoreCase(checkedTagName))
                .findAny()
                .isEmpty();
    }

    // Method checks whether a tag with the specified name exists in the database.
    // If so, the ID of the tag is returned.
    // If it doesn't, new tag will be created in database and return its ID.
    private int addTagToDBIfNotPresented(final String tagName) {
        Tag tmpTag;

        try {
            tmpTag = tagsService.getByName(tagName);
        } catch (EntityNotFoundException e) {
            tmpTag = tagsService.create(new TagDTO(tagName));
        }

        return tmpTag.getId();
    }

    public void deleteById(int id) {
        certificateDAO.deleteById(id);
    }
}