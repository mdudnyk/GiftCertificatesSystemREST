package com.epam.esm.services.impl;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;
import com.epam.esm.services.CertificatesTagsService;
import com.epam.esm.services.TagsService;
import com.epam.esm.services.exceptions.EntityNotFoundException;
import com.epam.esm.services.mappers.certificate.CertificateMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Myroslav Dudnyk
 */
@ExtendWith(MockitoExtension.class)
class CertificatesServiceImplTest {
    private static final int TEST_CERTIFICATE_ID = 1;
    private static final int TEST_TAG_ID = 1;
    private static final String TEST_TAG_NAME = "test_tag_name";

    @Mock
    private CertificateMapper certificateMapper;

    @Mock
    private CertificateDAO certificateDAO;

    @Mock
    private TagsService tagsService;

    @Mock
    private CertificatesTagsService certificatesTagsService;

    @InjectMocks
    private  CertificatesServiceImpl certificatesService;

    @Test
    void getByIdSuccess() {
        Certificate certificate = mock(Certificate.class);
        CertificateDTOResp expectedCertificateDTO = mock(CertificateDTOResp.class);
        List<String> tagNames = List.of(TEST_TAG_NAME);

        when(certificateDAO.getById(anyInt())).thenReturn(Optional.of(certificate));
        when(tagsService.getAllNamesByCertificateId(anyInt())).thenReturn(tagNames);
        when(certificateMapper.toDTO(certificate, tagNames)).thenReturn(expectedCertificateDTO);

        CertificateDTOResp resultCertificateDTO = certificatesService.getById(TEST_CERTIFICATE_ID);

        assertEquals(expectedCertificateDTO, resultCertificateDTO);
    }

    @Test
    void getByIdWhenCertificateNotFound() {
        when(certificateDAO.getById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificatesService.getById(TEST_CERTIFICATE_ID));
    }
}
