package com.epam.esm.services.impl;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.dtos.tag.TagDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;
import com.epam.esm.services.CertificatesTagsService;
import com.epam.esm.services.TagsService;
import com.epam.esm.services.exceptions.*;
import com.epam.esm.services.mappers.certificate.CertificateMapper;
import com.epam.esm.services.CertificatesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Myroslav Dudnyk
 */
@Service
public class CertificatesServiceImpl implements CertificatesService {
    private final String ENTITY_NAME = "certificate";

    private final CertificatesTagsService certificatesTagsService;

    private final CertificateMapper certificateMapper;

    private final CertificateDAO certificateDAO;

    private final TagsService tagsService;

    public CertificatesServiceImpl(CertificatesTagsService certificatesTagsService,
                                   CertificateMapper certificateMapper,
                                   CertificateDAO certificateDAO,
                                   TagsService tagsService) {
        this.certificatesTagsService = certificatesTagsService;
        this.certificateMapper = certificateMapper;
        this.certificateDAO = certificateDAO;
        this.tagsService = tagsService;
    }

    @Override
    public CertificateDTOResp getById(int id) {
        Certificate certificate = certificateDAO.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        List<String> tagNames = tagsService.getAllNamesByCertificateId(id);

        return certificateMapper.toDTO(certificate, tagNames);
    }

    @Override
    public List<CertificateDTOResp> getCertificates(String tagName, String searchText, String sortBy, String sortOrder) {
        List<Certificate> certificates = getCertificatesByTagNameOrGetAll(tagName);
        certificates = filterCertificatesBySearchText(certificates, searchText);
        sortCertificates(certificates, sortBy, sortOrder);

        return certificates.stream()
                .map(cert -> certificateMapper
                        .toDTO(cert, tagsService.getAllNamesByCertificateId(cert.getId())))
                .collect(Collectors.toList());
    }

    private List<Certificate> getCertificatesByTagNameOrGetAll(String tagName) {
        if (StringUtils.isNotEmpty(tagName)) {
            return certificateDAO.getCertificatesByTagName(tagName)
                    .orElseThrow(() ->
                            new EntityNotFoundException("Requested certificates not found (tag name = '"
                                    + tagName + "')"));
        }

        return certificateDAO.getAll()
                .orElseThrow(() -> new EntityNotFoundException("Certificates not found"));
    }

    private List<Certificate> filterCertificatesBySearchText(List<Certificate> certificates, String searchText) {
        if (StringUtils.isNotEmpty(searchText)) {
            return Optional.of(certificates.stream()
                            .filter(cert -> cert.getName().contains(searchText) || cert.getDescription().contains(searchText))
                            .collect(Collectors.toList()))
                    .orElseThrow(() ->
                            new EntityNotFoundException("Requested certificates not found (part of name/description = '"
                                    + searchText + "')"));
        }

        return certificates;
    }

    private void sortCertificates(List<Certificate> certificates, String sortBy, String sortOrder) {
        try {
            SortBy sortByEnum = StringUtils.isNotEmpty(sortBy)
                    ? SortBy.valueOf(sortBy.toUpperCase()) : SortBy.ID;

            SortOrder sortOrderEnum = StringUtils.isNotEmpty(sortOrder)
                    ? SortOrder.valueOf(sortOrder.toUpperCase()) : SortOrder.ASC;

            switch (sortOrderEnum) {
                case ASC -> certificates.sort(sortByEnum.comparator);
                case DESC -> certificates.sort(sortByEnum.comparator.reversed());
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedSortingParameter();
        }
    }

    @Transactional
    @Override
    public CertificateDTOResp create(CertificateDTOReq certificateDTOReq) {
        if (certificateDAO.checkIfCertificateWithNameExists(certificateDTOReq.name())) {
            throw new EntityAlreadyExistsException(ENTITY_NAME, certificateDTOReq.name());
        }

        int newCertificateId = (int) certificateDAO.create(certificateMapper.toEntity(certificateDTOReq))
                .orElseThrow(() ->
                        new ServiceException("Unable to get an ID of created certificate", 40023)
                );
        certificateDTOReq.tags().forEach(tagName -> addTagToCertificateEntity(newCertificateId, tagName));

        return getById(newCertificateId);
    }

    private void addTagToCertificateEntity(int certificateId, String tagName) {
        int tagId;

        try {
            tagId = tagsService.getTagIdByName(tagName);
        } catch (EntityNotFoundException e) {
            tagId = tagsService.create(new TagDTOReq(tagName)).id();
        }

        certificatesTagsService.create(certificateId, tagId);
    }

    @Transactional
    public CertificateDTOResp update(int certificateId, CertificateDTOReq certificateDTOReq) {
//        // Retrieve the old certificate entity from the database
//        Certificate certificateOldEntity = getById(certificateId);
//
//        // Update the certificate tags if present in the new entity
//        if (certificateNewEntity.tags() != null) {
//            updateCertificateTags(certificateNewEntity, certificateOldEntity);
//        }
//
//        // Update the fields of the new entity with values from the old entity, if they are null
//        int fieldsToUpdateCount =
//                fillNullFieldsOfNewEntityWithFieldsFromOldEntity(certificateNewEntity, certificateOldEntity);
//
//        // If any fields were updated, update the certificate entity in the database
//        if (fieldsToUpdateCount > 0) {
//            certificateDAO.update(certificateId, certificateNewEntity);
//        }
//
//        // Return the updated certificate entity from the database
        return getById(certificateId);
    }
//
//    // Fills null fields of certificateNewEntity with values from certificateOldEntity
//    // and returns the number of updated fields
//    private int fillNullFieldsOfNewEntityWithFieldsFromOldEntity(final CertificateDTOReq certificateNew, final Certificate certificateOld) {
//        return 0;
//    }
//
//    // Helper method that updates a field of a certificate entity
//    private <T> int updateField(Supplier<T> newValueGetter, Supplier<T> oldValueGetter, Consumer<T> valueSetter) {
//        T newValue = newValueGetter.get();
//        T oldValue = oldValueGetter.get();
//
//        if (newValue != null && !newValue.equals(oldValue)) {
//            valueSetter.accept(newValue);
//            return 1;
//        } else if (newValue == null) {
//            valueSetter.accept(oldValue);
//            return 1;
//        }
//
//        return 0;
//    }
//
//    // Updates the tags of a certificate based on the passed in CertificateDTO and existing Certificate
//    private void updateCertificateTags(final CertificateDTOReq certificateNewEntity, final Certificate certificateOldEntity) {
//        // Get the list of tags from the old certificate entity
//        List<Tag> tagListFromOldEntity = certificateOldEntity.getTags();
//
//        // Get the list of tag names from the new certificate DTO
//        List<String> tagNameListFromNewEntity = certificateNewEntity.tags();
//
//        // Get the list of tag names from the old certificate entity
//        List<String> tagNameListFromOldEntity = tagListFromOldEntity.stream().map(Tag::getName).toList();
//
//        // Get the ID of the certificate being updated
//        int certificateId = certificateOldEntity.getId();
//
//
//        // Iterate over each tag name in the list of tag names from the new certificate entity
//        for (String tagName : tagNameListFromNewEntity) {
//            // If the tag name is not in the list of tag names from the old certificate entity
//            if (isTagNameNotInTagNameList(tagName, tagNameListFromOldEntity)) {
//                // Add the new tag to the database (only if it doesn't exist in the database) and return its ID
//                int tagId = addTagToDBIfNotPresented(tagName);
//                // Add the tag to the certificate entity
//                certificatesTagsService.create(certificateId, tagId);
//            }
//        }
//
//        // Iterate over each tag in the list of tags from the old certificate entity
//        for (Tag tag : tagListFromOldEntity) {
//            // If the tag is not in the list of tag names from the new certificate entity
//            if (isTagNameNotInTagNameList(tag.getName(), tagNameListFromNewEntity)) {
//                // Remove the tag from the certificate entity
//                certificatesTagsService
//                        .deleteByCertificateIdAndTagId(certificateId, tag.getId());
//            }
//        }
//    }
//
//    // Method checks if the tag name does not exist in the list of tag names. If so, 'true' is returned.
//    private boolean isTagNameNotInTagNameList(final String checkedTagName, final List<String> tagNameList) {
//        return tagNameList.stream().filter(tagName -> tagName.equalsIgnoreCase(checkedTagName))
//                .findAny()
//                .isEmpty();
//    }


    public void deleteById(int id) {
        int deletedRows = certificateDAO.deleteById(id);

        if (deletedRows == 0) {
            throw new EntityNotDeletedException(ENTITY_NAME, id);
        }
    }

    public enum SortBy {
        ID(Comparator
                .comparing(certificate -> certificate.getId())),
        NAME(Comparator
                .comparing(certificate -> certificate.getName())),
        DATE(Comparator
                .comparing(certificate -> certificate.getCreateDate()));

        private final Comparator<Certificate> comparator;

        SortBy(final Comparator<Certificate> comparator) {
            this.comparator = comparator;
        }
    }

    public enum SortOrder {
        ASC,
        DESC
    }
}
