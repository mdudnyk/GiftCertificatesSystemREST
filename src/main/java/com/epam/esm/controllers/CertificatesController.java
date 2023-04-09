package com.epam.esm.controllers;

import com.epam.esm.models.Certificate;
import com.epam.esm.models.dtos.CertificateDTO;
import com.epam.esm.services.CertificatesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@RestController
@RequestMapping(value = "/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificatesController {
    private final CertificatesService certificatesService;

    public CertificatesController(CertificatesService certificatesService) {
        this.certificatesService = certificatesService;
    }

    @GetMapping()
    public List<Certificate> getAll() {
        return certificatesService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getById(@PathVariable("id") int id) {
        Certificate certificate = certificatesService.getById(id);
        return ResponseEntity.ok(certificate);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Certificate> create(@RequestBody CertificateDTO certificate) {
        Certificate createdCertificate = certificatesService.create(certificate);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(createdCertificate.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdCertificate);
    }

    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Certificate> update(@RequestBody CertificateDTO certificate, @PathVariable final int id) {
        Certificate updatedCertificate = certificatesService.update(id, certificate);
        String location = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();

        return ResponseEntity.ok()
                .header(HttpHeaders.LOCATION, location)
                .body(updatedCertificate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        certificatesService.deleteById(id);
    }
}