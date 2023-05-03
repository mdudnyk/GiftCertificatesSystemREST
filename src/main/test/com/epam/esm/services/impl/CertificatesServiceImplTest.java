package com.epam.esm.services.impl;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;
import com.epam.esm.services.CertificatesTagsService;
import com.epam.esm.services.TagsService;
import com.epam.esm.services.exceptions.EntityNotDeletedException;
import com.epam.esm.services.exceptions.EntityNotFoundException;
import com.epam.esm.services.exceptions.UnsupportedSortingParameter;
import com.epam.esm.services.mappers.certificate.CertificateMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author Myroslav Dudnyk
 */
@ExtendWith(MockitoExtension.class)
class CertificatesServiceImplTest {
    private static final String TEST_TAG_NAME = "test_tag_name";

    private static final int TEST_CERTIFICATE_ID = 1;
    private static final int SECOND_TEST_CERTIFICATE_ID = 2;
    private static final String TEST_CERTIFICATE_NAME = "1'st_test_name";
    private static final String SECOND_TEST_CERTIFICATE_NAME = "2'nd_test_name";
    private static final List<String> TEST_TAG_NAMES_LIST = singletonList(TEST_TAG_NAME);
    private static final String TEST_CERTIFICATE_DESCRIPTION = "test_certificate_description";
    private static final BigDecimal TEST_CERTIFICATE_PRICE = BigDecimal.TEN;
    private static final int TEST_CERTIFICATE_DURATION = 1;

    private static final Certificate TEST_CERTIFICATE;
    private static final Certificate SECOND_TEST_CERTIFICATE;
    private static final CertificateDTOResp TEST_CERTIFICATE_DTO_RESP;
    private static final CertificateDTOResp SECOND_TEST_CERTIFICATE_DTO_RESP;


    static {
        TEST_CERTIFICATE = new Certificate(
                TEST_CERTIFICATE_NAME,
                TEST_CERTIFICATE_DESCRIPTION,
                TEST_CERTIFICATE_PRICE,
                TEST_CERTIFICATE_DURATION
        );
        TEST_CERTIFICATE.setId(TEST_CERTIFICATE_ID);
        TEST_CERTIFICATE.setCreateDate(LocalDateTime.now().minusDays(2));

        TEST_CERTIFICATE_DTO_RESP = new CertificateDTOResp(
                TEST_CERTIFICATE_ID,
                TEST_CERTIFICATE_NAME,
                TEST_TAG_NAMES_LIST,
                TEST_CERTIFICATE_DESCRIPTION,
                TEST_CERTIFICATE_PRICE,
                TEST_CERTIFICATE_DURATION
        );

        SECOND_TEST_CERTIFICATE = new Certificate(
                SECOND_TEST_CERTIFICATE_NAME,
                TEST_CERTIFICATE_DESCRIPTION,
                TEST_CERTIFICATE_PRICE,
                TEST_CERTIFICATE_DURATION
        );
        SECOND_TEST_CERTIFICATE.setId(SECOND_TEST_CERTIFICATE_ID);
        SECOND_TEST_CERTIFICATE.setCreateDate(LocalDateTime.now());

        SECOND_TEST_CERTIFICATE_DTO_RESP = new CertificateDTOResp(
                SECOND_TEST_CERTIFICATE_ID,
                SECOND_TEST_CERTIFICATE_NAME,
                TEST_TAG_NAMES_LIST,
                TEST_CERTIFICATE_DESCRIPTION,
                TEST_CERTIFICATE_PRICE,
                TEST_CERTIFICATE_DURATION
        );
    }

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
    void testGetByIdSuccess() {
        when(certificateDAO.getById(anyInt())).thenReturn(Optional.of(TEST_CERTIFICATE));
        when(tagsService.getAllNamesByCertificateId(anyInt())).thenReturn(TEST_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(any(Certificate.class), anyList())).thenReturn(TEST_CERTIFICATE_DTO_RESP);

        assertEquals(TEST_CERTIFICATE_DTO_RESP, certificatesService.getById(TEST_CERTIFICATE_ID));
        verify(certificateDAO).getById(anyInt());
        verify(tagsService).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper).toDTO(any(Certificate.class), anyList());
    }

    @Test
    void testGetByIdWhenCertificateNotFound() {
        when(certificateDAO.getById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificatesService.getById(TEST_CERTIFICATE_ID));
        verify(certificateDAO).getById(anyInt());
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
    }

    @Test
    void testGetCertificatesSuccessAllArgumentsAreNull() {
        when(certificateDAO.getAll()).thenReturn(Optional.of(singletonList(new Certificate())));
        when(tagsService.getAllNamesByCertificateId(anyInt())).thenReturn(TEST_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(any(Certificate.class), anyList())).thenReturn(TEST_CERTIFICATE_DTO_RESP);

        List<CertificateDTOResp> actualResult = certificatesService
                .getCertificates(null, null, null, null);
        List<CertificateDTOResp> expectedResult = Collections.singletonList(TEST_CERTIFICATE_DTO_RESP);

        assertEquals(expectedResult, actualResult);
        verify(certificateDAO, times(1)).getAll();
        verify(tagsService, times(1)).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, times(1)).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO, never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesAllArgumentsAreNullThrowsEntityNotFoundException() {
        when(certificateDAO.getAll()).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificatesService
                .getCertificates(null, null, null, null));
        verify(certificateDAO, times(1)).getAll();
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO, never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesByTagNameSuccess() {
        when(certificateDAO.getCertificatesByTagName(anyString()))
                .thenReturn(Optional.of(singletonList(new Certificate())));
        when(tagsService.getAllNamesByCertificateId(anyInt())).thenReturn(TEST_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(any(Certificate.class), anyList())).thenReturn(TEST_CERTIFICATE_DTO_RESP);

        List<CertificateDTOResp> actualResult = certificatesService
                .getCertificates(TEST_TAG_NAME, null, null, null);
        List<CertificateDTOResp> expectedResult = Collections.singletonList(TEST_CERTIFICATE_DTO_RESP);

        assertEquals(expectedResult, actualResult);
        verify(certificateDAO,times(1)).getCertificatesByTagName(anyString());
        verify(tagsService, times(1)).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, times(1)).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO,never()).getAll();
    }

    @Test
    void testGetCertificatesByTagNameThrowsEntityNotFoundException() {
        when(certificateDAO.getCertificatesByTagName(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificatesService
                .getCertificates(TEST_TAG_NAME, null, null, null));
        verify(certificateDAO,times(1)).getCertificatesByTagName(anyString());
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO,never()).getAll();
    }

    @Test
    void testGetCertificatesByPartOfNameOrDescriptionSuccess() {
        when(certificateDAO.getAll())
                .thenReturn(Optional.of(singletonList(TEST_CERTIFICATE)));
        when(tagsService.getAllNamesByCertificateId(anyInt())).thenReturn(TEST_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(any(Certificate.class), anyList())).thenReturn(TEST_CERTIFICATE_DTO_RESP);

        List<CertificateDTOResp> actualResult = certificatesService
                .getCertificates(null, StringUtils.truncate(TEST_CERTIFICATE_DESCRIPTION, 2),
                        null, null);
        List<CertificateDTOResp> expectedResult = Collections.singletonList(TEST_CERTIFICATE_DTO_RESP);

        assertEquals(expectedResult, actualResult);
        verify(certificateDAO,times(1)).getAll();
        verify(tagsService, times(1)).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, times(1)).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO,never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesByPartOfNameOrDescriptionThrowsEntityNotFoundException() {
        when(certificateDAO.getAll())
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificatesService
                .getCertificates(null, TEST_CERTIFICATE_DESCRIPTION + "test", null, null));
        verify(certificateDAO,times(1)).getAll();
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO,never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesWithSortingSeparatelyByNameAndDateSuccess() {
        List<CertificateDTOResp> expectedResult = List.of(TEST_CERTIFICATE_DTO_RESP, SECOND_TEST_CERTIFICATE_DTO_RESP);
        List<Certificate> certificates = new ArrayList<>(List.of(SECOND_TEST_CERTIFICATE, TEST_CERTIFICATE));

        when(certificateDAO.getAll())
                .thenReturn(Optional.of(certificates));
        when(tagsService.getAllNamesByCertificateId(anyInt())).thenReturn(TEST_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(TEST_CERTIFICATE, TEST_TAG_NAMES_LIST)).thenReturn(TEST_CERTIFICATE_DTO_RESP);
        when(certificateMapper.toDTO(SECOND_TEST_CERTIFICATE, TEST_TAG_NAMES_LIST)).thenReturn(SECOND_TEST_CERTIFICATE_DTO_RESP);

        // Sorting By ID enabled by default
        assertEquals(expectedResult, certificatesService
                .getCertificates(null, null, null, null));

        assertEquals(expectedResult, certificatesService
                .getCertificates(null, null, "name", null));

        assertEquals(expectedResult, certificatesService
                .getCertificates(null, null, "date", null));

        verify(certificateDAO,times(3)).getAll();
        verify(tagsService, times(6)).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, times(6)).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO,never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesWithSortingByThrowsUnsupportedSortingParameterException() {
        List<Certificate> certificates = new ArrayList<>(List.of(SECOND_TEST_CERTIFICATE, TEST_CERTIFICATE));

        when(certificateDAO.getAll()).thenReturn(Optional.of(certificates));

        assertThrows(UnsupportedSortingParameter.class, ()-> certificatesService
                .getCertificates(null, null, "invalidParameter", null));

        verify(certificateDAO,times(1)).getAll();
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO,never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesWithOrderingSeparatelyByASCAndDESCSuccess() {
        List<CertificateDTOResp> expectedResult = List.of(TEST_CERTIFICATE_DTO_RESP, SECOND_TEST_CERTIFICATE_DTO_RESP);
        List<Certificate> certificates = new ArrayList<>(List.of(TEST_CERTIFICATE, SECOND_TEST_CERTIFICATE));

        when(certificateDAO.getAll())
                .thenReturn(Optional.of(certificates));
        when(tagsService.getAllNamesByCertificateId(anyInt())).thenReturn(TEST_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(TEST_CERTIFICATE, TEST_TAG_NAMES_LIST)).thenReturn(TEST_CERTIFICATE_DTO_RESP);
        when(certificateMapper.toDTO(SECOND_TEST_CERTIFICATE, TEST_TAG_NAMES_LIST)).thenReturn(SECOND_TEST_CERTIFICATE_DTO_RESP);

        assertEquals(expectedResult, certificatesService
                .getCertificates(null, null, null, "asc"));
        assertNotEquals(expectedResult, certificatesService
                .getCertificates(null, null, null, "desc"));

        verify(certificateDAO,times(2)).getAll();
        verify(tagsService, times(4)).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, times(4)).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO,never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesWithOrderingSeparatelyByASCAndDESCThrowsUnsupportedSortingParameterException() {
        List<Certificate> certificates = new ArrayList<>(List.of(SECOND_TEST_CERTIFICATE, TEST_CERTIFICATE));

        when(certificateDAO.getAll()).thenReturn(Optional.of(certificates));

        assertThrows(UnsupportedSortingParameter.class, ()-> certificatesService
                .getCertificates(null, null, null, "invalidOrder"));

        verify(certificateDAO,times(1)).getAll();
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO,never()).getCertificatesByTagName(anyString());
    }


    @Test
    void testDeleteByIdSuccess() {
        when(certificateDAO.deleteById(anyInt())).thenReturn(1);

        certificatesService.deleteById(TEST_CERTIFICATE_ID);

        verify(certificateDAO).deleteById(anyInt());
    }

    @Test
    void testDeleteByIdThrowsEntityNotDeletedException() {
        when(certificateDAO.deleteById(anyInt())).thenReturn(0);

        assertThrows(EntityNotDeletedException.class, () -> certificatesService.deleteById(TEST_CERTIFICATE_ID));
        verify(certificateDAO).deleteById(anyInt());
    }
}
