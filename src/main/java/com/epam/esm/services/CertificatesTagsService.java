package com.epam.esm.services;

import com.epam.esm.dao.CertificateTagDAO;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Integer> getTagsIdByCertificateId(int certificateId) {
        return certificateTagDAO.getTagsIdByCertificateId(certificateId);
    }
}
