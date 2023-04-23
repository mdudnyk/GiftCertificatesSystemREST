package com.epam.esm.controllers;

import com.epam.esm.models.dtos.certificate.CertificateDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;
import com.epam.esm.services.CertificatesService;
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
        List<CertificateDTOResp> certificates =
                certificatesService.getCertificates(tagName, searchText, sortBy, sortOrder);

        return new ResponseEntity<>(certificates, HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CertificateDTOResp> create(@RequestBody CertificateDTOReq certificateDTOReq) {
        CertificateDTOResp createdCertificate = certificatesService.create(certificateDTOReq);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(createdCertificate.id())
                .toUri();

        return ResponseEntity.created(location).body(createdCertificate);
    }

//    @PatchMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
//    public ResponseEntity<CertificateDTOResp> update(@PathVariable int id, @RequestBody CertificateDTOReq certificate) {
//        CertificateDTOResp updatedCertificate = certificatesServiceImpl.update(id, certificate);
//        String location = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.LOCATION, location)
//                .body(updatedCertificate);
//    }

//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable("id") int id) {
//        certificatesServiceImpl.deleteById(id);
//    }
}