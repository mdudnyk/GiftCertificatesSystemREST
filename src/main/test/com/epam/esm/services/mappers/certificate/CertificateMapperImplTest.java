package com.epam.esm.services.mappers.certificate;

import com.epam.esm.models.Certificate;
import com.epam.esm.models.dtos.certificate.CertificateDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Myroslav Dudnyk
 */
class CertificateMapperImplTest {

    private static final int CERTIFICATE_ID = 1;
    private static final String CERTIFICATE_NAME = "Name";
    private static final List<String> TAG_NAMES_LIST = null;
    private static final String CERTIFICATE_DESCRIPTION = "Description";
    private static final BigDecimal CERTIFICATE_PRICE = BigDecimal.TEN;
    private static final int CERTIFICATE_DURATION = 1;

    private static final Certificate TEST_CERTIFICATE;
    private static final CertificateDTOResp TEST_CERTIFICATE_DTO_RESP;
    private static final CertificateDTOReq CERTIFICATE_DTO_REQ;

    private static final CertificateMapper certificateMapper;

    static {
        certificateMapper = new CertificateMapperImpl();

        CERTIFICATE_DTO_REQ = new CertificateDTOReq(
                CERTIFICATE_NAME,
                TAG_NAMES_LIST,
                CERTIFICATE_DESCRIPTION,
                CERTIFICATE_PRICE,
                CERTIFICATE_DURATION
        );

        TEST_CERTIFICATE = new Certificate(
                CERTIFICATE_NAME,
                CERTIFICATE_DESCRIPTION,
                CERTIFICATE_PRICE,
                CERTIFICATE_DURATION
        );
        TEST_CERTIFICATE.setId(CERTIFICATE_ID);

        TEST_CERTIFICATE_DTO_RESP = new CertificateDTOResp(
                CERTIFICATE_ID,
                CERTIFICATE_NAME,
                TAG_NAMES_LIST,
                CERTIFICATE_DESCRIPTION,
                CERTIFICATE_PRICE,
                CERTIFICATE_DURATION
        );
    }

    @Test
    void toEntity() {
        Certificate certificate = certificateMapper.toEntity(CERTIFICATE_DTO_REQ);
        assertEquals(TEST_CERTIFICATE, certificate);
    }

    @Test
    void toDTO() {
        CertificateDTOResp certificateDTOResp = certificateMapper.toDTO(TEST_CERTIFICATE, TAG_NAMES_LIST);
        assertEquals(TEST_CERTIFICATE_DTO_RESP, certificateDTOResp);
    }

    @Test
    void toUpdatedEntity() {
        CertificateDTOResp certificateDTOOld = new CertificateDTOResp(
                CERTIFICATE_ID,
                "Old Name",
                null,
                "Old Description",
                BigDecimal.ONE,
                1
        );

        Certificate certificate = certificateMapper.toUpdatedEntity(CERTIFICATE_DTO_REQ, certificateDTOOld);
        assertEquals(TEST_CERTIFICATE, certificate);
    }
}
