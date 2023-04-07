package com.epam.esm.services;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.CertificateDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@Service
public class CertificatesService {
    private final CertificateDAO certificateDAO;

    public CertificatesService(CertificateDAO certificateDAO) {
        this.certificateDAO = certificateDAO;
    }

    public List<Certificate> getAll() {
        return certificateDAO.getAll();
    }

    public Certificate getById(int id) {
        return certificateDAO.getById(id);
    }

    public Certificate create(CertificateDTO certificate) {
        int newCertificateId = certificateDAO.create(certificate);
        return getById(newCertificateId);
    }

    public void deleteById(int id) {
        certificateDAO.deleteById(id);
    }
}