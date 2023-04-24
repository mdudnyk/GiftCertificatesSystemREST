package com.epam.esm.services.impl;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.Tag;
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

import javax.swing.*;
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
        certificateDTOReq.tags().forEach(tagName -> attachTagToCertificate(tagName, newCertificateId));

        return getById(newCertificateId);
    }

    private void attachTagToCertificate(String tagName, int certificateId) {
        int tagId;

        try {
            tagId = tagsService.getTagIdByName(tagName);
        } catch (EntityNotFoundException e) {
            tagId = tagsService.create(new TagDTOReq(tagName)).id();
        }

        certificatesTagsService.attachTagToCertificate(certificateId, tagId);
    }

    @Transactional
    public CertificateDTOResp update(int certId, CertificateDTOReq certDTOReq) {
        CertificateDTOResp certOldEntity = getById(certId);

        if (certDTOReq.tags() != null) {
            updateCertificateTags(certId, certOldEntity.tags(), certDTOReq.tags());
        }

        return getById(certId);
    }

    private void updateCertificateTags(int certId, List<String> oldTagNamesList, List<String> newTagNamesList) {
        // Remove any tags from the old entity that are not in the new entity
        List<String> tagNamesToRemove = getTagsToRemove(oldTagNamesList, newTagNamesList);
        tagNamesToRemove.forEach(tagName -> certificatesTagsService
                .deleteTagFromCertificate(tagsService.getTagIdByName(tagName), certId));

        // Add all tags from the new entity that are not in the old entity
        List<String> tagNamesToAttach = getTagsToAttach(oldTagNamesList, newTagNamesList);
        tagNamesToAttach.forEach(tagName -> attachTagToCertificate(tagName, certId));
    }

    private List<String> getTagsToRemove(List<String> oldTagNamesList, List<String> newTagNamesList) {
        return oldTagNamesList.stream()
                .filter(oldTagName -> !newTagNamesList.contains(oldTagName))
                .toList();
    }

    private List<String> getTagsToAttach(List<String> oldTagNamesList, List<String> newTagNamesList) {
        return newTagNamesList.stream()
                .filter(newTagName -> !oldTagNamesList.contains(newTagName))
                .toList();
    }

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
