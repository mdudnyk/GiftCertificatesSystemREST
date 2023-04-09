package com.epam.esm.services;

import com.epam.esm.dao.CertificateTagDAO;
import org.springframework.stereotype.Service;

/**
 * @author Myroslav Dudnyk
 */
@Service
public class CertificatesTagsService {
    private final CertificateTagDAO certificateTagDAO;

    public CertificatesTagsService(final CertificateTagDAO certificateTagDAO) {
        this.certificateTagDAO = certificateTagDAO;
    }

    public int create(int certificateId, int tagId) {
        return certificateTagDAO.create(certificateId, tagId);
    }

    public void deleteByCertificateIdAndTagId(int certificateId, int tagId) {
        certificateTagDAO.deleteByCertificateIdAndTagId(certificateId, tagId);
    }
}
