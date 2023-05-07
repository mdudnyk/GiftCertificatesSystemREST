package com.epam.esm.controllers;

import com.epam.esm.models.dtos.certificate.CertificateDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;
import com.epam.esm.services.CertificatesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Myroslav Dudnyk
 */
@RestController
@RequestMapping(value = "/certificates", produces = APPLICATION_JSON_VALUE)
public class CertificatesController {
    private final CertificatesService certificatesService;

    public CertificatesController(CertificatesService certificatesService) {
        this.certificatesService = certificatesService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateDTOResp> getById(@PathVariable("id") int id) {
        return ResponseEntity.ok(certificatesService.getById(id));
    }

    @GetMapping()
    public ResponseEntity<List<CertificateDTOResp>> getCertificates(
            @RequestParam(value = "tagName", required = false) String tagName,
            @RequestParam(value = "searchText", required = false) String searchText,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder) {

        return ResponseEntity.ok(certificatesService
                .getCertificates(tagName, searchText, sortBy, sortOrder));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTOResp> create(@RequestBody CertificateDTOReq certificateDTOReq) {
        CertificateDTOResp createdCertificate = certificatesService.create(certificateDTOReq);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(createdCertificate.id())
                .toUri();

        return ResponseEntity.created(location).body(createdCertificate);
    }

    @PatchMapping(value = "/{id}", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTOResp> update(@PathVariable int id,
                                                     @RequestBody CertificateDTOReq certificateDTOReq) {
        CertificateDTOResp updatedCertificate = certificatesService.update(id, certificateDTOReq);
        String location = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();

        return ResponseEntity.ok()
                .header(LOCATION, location)
                .body(updatedCertificate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        certificatesService.deleteById(id);
    }
}
