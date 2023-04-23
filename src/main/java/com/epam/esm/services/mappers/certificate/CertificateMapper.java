package com.epam.esm.services.mappers.certificate;

import com.epam.esm.models.Certificate;
import com.epam.esm.models.dtos.certificate.CertificateDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;

import java.util.List;

/**
 * @author Myroslav Dudnyk
 */

public interface CertificateMapper {
    Certificate toEntity(CertificateDTOReq certificateDTOReq);

    CertificateDTOResp toDTO(Certificate certificate, List<String> tagNames);
}
