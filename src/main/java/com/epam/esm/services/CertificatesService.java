package com.epam.esm.services;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.dao.exceptions.EntityNotFoundException;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.CertificateDTO;
import com.epam.esm.models.dtos.TagDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Certificate> getAll() {
        List<Certificate> certificates = certificateDAO.getAll();

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
    public Certificate update(int certificateId, CertificateDTO certificateDTO) {
        Certificate certificateBeforeUpdate = getById(certificateId);

        if (certificateDTO.getTags() != null) {
            updateCertificateTags(certificateDTO, certificateBeforeUpdate);
        }

        certificateDAO.update(certificateId, certificateDTO);

        return getById(certificateId);
    }

    private void updateCertificateTags(final CertificateDTO certificateDTO, final Certificate certificateBeforeUpdate) {
        List<Tag> tagsListBeforeUpdate = certificateBeforeUpdate.getTags();
        List<String> tagNamesListForUpdate = certificateDTO.getTags();
        int certificateId = certificateBeforeUpdate.getId();

        for (String tagName : tagNamesListForUpdate) {
            boolean tagIsNotPresentedInVersionBeforeUpdate = tagsListBeforeUpdate.stream()
                    .filter(tag -> tag.getName().equalsIgnoreCase(tagName))
                    .findAny()
                    .isEmpty();
            if (tagIsNotPresentedInVersionBeforeUpdate) {
                int tagId = addTagToDBIfNotPresented(tagName);
                certificatesTagsService
                        .create(certificateId, tagId);
            }
        }

        for (Tag tag : tagsListBeforeUpdate) {
            boolean tagIsNotPresentedInNewVersion = tagNamesListForUpdate.stream()
                    .filter(tagName -> tagName.equalsIgnoreCase(tag.getName()))
                    .findAny()
                    .isEmpty();
            if (tagIsNotPresentedInNewVersion) {
                certificatesTagsService
                        .deleteByCertificateIdAndTagId(certificateId, tag.getId());
            }
        }
    }

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