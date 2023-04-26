package com.epam.esm.services.mappers.certificate;

import com.epam.esm.models.Certificate;
import com.epam.esm.models.dtos.certificate.CertificateDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

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

    @Override
    public Certificate toUpdatedEntity(final CertificateDTOReq certDTOReq, final CertificateDTOResp certOldEntity) {
        return new Certificate(
                isEmpty(certDTOReq.name()) ? certOldEntity.name() : certDTOReq.name(),
                isEmpty(certDTOReq.description()) ? certOldEntity.description() : certDTOReq.description(),
                certDTOReq.price() == null ? certOldEntity.price() : certDTOReq.price(),
                certDTOReq.duration() == null ? certOldEntity.duration() : certDTOReq.duration()
        );
    }
}
