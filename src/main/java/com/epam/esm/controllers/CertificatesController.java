package com.epam.esm.controllers;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.models.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificatesController {
    private final CertificateDAO certificateDAO;

    @Autowired
    public CertificatesController(CertificateDAO certificateDAO) {
        this.certificateDAO = certificateDAO;
    }

    @GetMapping()
    public List<Certificate> allCertificates() {
        return certificateDAO.getAll();
    }

    @GetMapping("/{id}")
    public Certificate getById(@PathVariable("id") int id) {
        return certificateDAO.getById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Certificate newCertificate(@RequestBody Certificate certificate) {
        int newCertificateId = certificateDAO.create(certificate);
        return certificateDAO.getById(newCertificateId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        certificateDAO.delete(id);
    }
}
