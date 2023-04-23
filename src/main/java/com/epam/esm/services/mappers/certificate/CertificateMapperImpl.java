package com.epam.esm.services.mappers.certificate;

import com.epam.esm.models.Certificate;
import com.epam.esm.models.dtos.certificate.CertificateDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@Component
public class CertificateMapperImpl implements CertificateMapper {

    @Override
    public Certificate toEntity(final CertificateDTOReq certificateDTOReq) {
        return new Certificate(
                certificateDTOReq.name(),
                certificateDTOReq.description(),
                certificateDTOReq.price(),
                certificateDTOReq.duration());
    }

    @Override
    public CertificateDTOResp toDTO(final Certificate certificate, final List<String> tagNames) {
        return new CertificateDTOResp(
                certificate.getId(),
                certificate.getName(),
                tagNames,
                certificate.getDescription(),
                certificate.getPrice(),
                certificate.getDuration()
        );
    }
}
