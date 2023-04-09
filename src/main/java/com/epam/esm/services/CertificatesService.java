package com.epam.esm.services;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.dao.CertificateTagDAO;
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
        Tag tmpTag;
        for (String tagName : certificate.getTags()) {
            try {
                tmpTag = tagsService.getByName(tagName);
            } catch (EntityNotFoundException e) {
                tmpTag = tagsService.create(new TagDTO(tagName));
            }
            certificatesTagsService.create(newCertificateId, tmpTag.getId());
        }

        return getById(newCertificateId);
    }

    public void deleteById(int id) {
        certificateDAO.deleteById(id);
    }
}