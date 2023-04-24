package com.epam.esm.services.impl;

import com.epam.esm.dao.CertificateTagDAO;
import com.epam.esm.services.CertificatesTagsService;
import org.springframework.stereotype.Service;

/**
 * @author Myroslav Dudnyk
 */
@Service
public class CertificatesTagsServiceImpl implements CertificatesTagsService {
    private final CertificateTagDAO certificateTagDAO;

    public CertificatesTagsServiceImpl(final CertificateTagDAO certificateTagDAO) {
        this.certificateTagDAO = certificateTagDAO;
    }

    @Override
    public void attachTagToCertificate(int tagId, int certificateId) {
        certificateTagDAO.create(certificateId, tagId);
    }

    public void deleteTagFromCertificate(int tagId, int certificateId) {
        certificateTagDAO.deleteByCertificateIdAndTagId(certificateId, tagId);
    }
}
