package com.epam.esm.services;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.dao.exceptions.EntityNotFoundException;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.CertificateDTO;
import com.epam.esm.models.dtos.TagDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        List<Certificate> certificates;

        // If tag name is specified, get certificates by tag name.
        if (tagName != null && !tagName.isEmpty()) {
            certificates = certificateDAO.getCertificatesByTagName(tagName);
        } else {
            // Otherwise, get all certificates.
            certificates = certificateDAO.getAll();
        }

        // If search text is specified, filter certificates by name or description.
        if (searchText != null && !searchText.isEmpty()) {
            certificates = certificates.stream()
                    .filter(cert -> cert.getName().contains(searchText) || cert.getDescription().contains(searchText))
                    .collect(Collectors.toList());
        }

        // Sort certificates by date or name.
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

        // Get tags list for every certificate.
        for (Certificate certificate : certificates) {
            certificate.setTags(tagsService.getTagsByCertificateId(certificate.getId()));
        }

        return certificates;
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
        Certificate certificateOldEntity = getById(certificateId);

        if (certificateNewEntity.getTags() != null) {
            updateCertificateTags(certificateNewEntity, certificateOldEntity);
        }

        int fieldsToUpdateCount =
                fillNullFieldsOfNewEntityWithFieldsFromOldEntity(certificateNewEntity, certificateOldEntity);
        if (fieldsToUpdateCount > 0) {
            certificateDAO.update(certificateId, certificateNewEntity);
        }

        return getById(certificateId);
    }

    // Fills the 'null' fields of certificateNewEntity with values from certificateOldEntity
    // and returns the number of updated fields.
    private int fillNullFieldsOfNewEntityWithFieldsFromOldEntity(final CertificateDTO certificateNewEntity,
                                                                 final Certificate certificateOldEntity) {
        int updatedFieldCount = 0;

        String newName = certificateNewEntity.getName();
        String oldName = certificateOldEntity.getName();
        if (newName != null && !newName.equals(oldName)) {
            updatedFieldCount++;
        } else {
            certificateNewEntity.setName(oldName);
        }

        String newDescription = certificateNewEntity.getDescription();
        String oldDescription = certificateOldEntity.getDescription();
        if (newDescription != null && !newDescription.equals(oldDescription)) {
            updatedFieldCount++;
        } else {
            certificateNewEntity.setDescription(oldDescription);
        }

        BigDecimal newPrice = certificateNewEntity.getPrice();
        BigDecimal oldPrice = certificateOldEntity.getPrice();
        if (newPrice != null && newPrice.compareTo(oldPrice) != 0) {
            updatedFieldCount++;
        } else {
            certificateNewEntity.setPrice(oldPrice);
        }

        Integer newDuration = certificateNewEntity.getDuration();
        Integer oldDuration = certificateOldEntity.getDuration();
        if (newDuration != null && newDuration.compareTo(oldDuration) != 0) {
            updatedFieldCount++;
        } else {
            certificateNewEntity.setDuration(oldDuration);
        }

        return updatedFieldCount;
    }

    // This method updates the tags of a certificate based on the passed in CertificateDTO and existing Certificate.
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