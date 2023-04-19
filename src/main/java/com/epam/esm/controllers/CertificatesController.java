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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Myroslav Dudnyk
 */

//Constants is better to static import
@RestController
@RequestMapping(value = "/certificates", produces = APPLICATION_JSON_VALUE)
public class CertificatesController {
    private final CertificatesService certificatesService;

    public CertificatesController(CertificatesService certificatesService) {
        this.certificatesService = certificatesService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getById(@PathVariable("id") int id) {
        Certificate certificate = certificatesService.getById(id);
        return ResponseEntity.ok(certificate);
    }

    @GetMapping()
    public ResponseEntity<List<Certificate>> getCertificates(
            @RequestParam(value = "tagName", required = false) String tagName,
            @RequestParam(value = "searchText", required = false) String searchText,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder) {
        List<Certificate> certificates =
                certificatesService.getCertificates(tagName, searchText, sortBy, sortOrder);

        if (certificates.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }

    //duplication of 'application/json'
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
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