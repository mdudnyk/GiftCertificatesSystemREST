package com.epam.esm.services.impl;

import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.models.Certificate;
import com.epam.esm.models.dtos.certificate.CertificateDTOReq;
import com.epam.esm.models.dtos.certificate.CertificateDTOResp;
import com.epam.esm.services.CertificatesTagsService;
import com.epam.esm.services.TagsService;
import com.epam.esm.services.exceptions.EntityNotDeletedException;
import com.epam.esm.services.exceptions.EntityNotFoundException;
import com.epam.esm.services.exceptions.UnsupportedSortingParameter;
import com.epam.esm.services.mappers.certificate.CertificateMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author Myroslav Dudnyk
 */
@ExtendWith(MockitoExtension.class)
class CertificatesServiceImplTest {
    private static final String TAG_NAME = "Tag1";

    private static final int CERTIFICATE_ID = 1;
    private static final int SECOND_CERTIFICATE_ID = 2;

    private static final String CERTIFICATE_NAME = "(B) First Name";
    private static final String SECOND_CERTIFICATE_NAME = "(A) Second Name";

    private static final List<String> TAG_NAMES_LIST = List.of(TAG_NAME, "Tag2", "Tag3");
    private static final List<String> SECOND_TAG_NAMES_LIST = List.of(TAG_NAME, "Tag4", "Tag5");

    private static final String CERTIFICATE_DESCRIPTION = "First Description";
    private static final String PART_OF_DESCRIPTION = "First";
    private static final String SECOND_CERTIFICATE_DESCRIPTION = "Second Description";

    private static final BigDecimal CERTIFICATE_PRICE = BigDecimal.TEN;
    private static final BigDecimal SECOND_CERTIFICATE_PRICE = BigDecimal.ONE;

    private static final int CERTIFICATE_DURATION = 10;
    private static final int SECOND_CERTIFICATE_DURATION = 1;

    private static final Certificate CERTIFICATE;
    private static final Certificate SECOND_CERTIFICATE;

    private static final CertificateDTOResp CERTIFICATE_DTO_RESP;
    private static final CertificateDTOResp SECOND_CERTIFICATE_DTO_RESP;

    private static final List<Certificate> CERTIFICATES;
    private static final List<CertificateDTOResp> CERTIFICATES_DTO_RESP;

    static {
        CERTIFICATE = new Certificate(
                CERTIFICATE_NAME,
                CERTIFICATE_DESCRIPTION,
                CERTIFICATE_PRICE,
                CERTIFICATE_DURATION
        );
        CERTIFICATE.setId(CERTIFICATE_ID);
        CERTIFICATE.setCreateDate(LocalDateTime.now().plusDays(1));

        SECOND_CERTIFICATE = new Certificate(
                SECOND_CERTIFICATE_NAME,
                SECOND_CERTIFICATE_DESCRIPTION,
                SECOND_CERTIFICATE_PRICE,
                SECOND_CERTIFICATE_DURATION
        );
        SECOND_CERTIFICATE.setId(SECOND_CERTIFICATE_ID);
        SECOND_CERTIFICATE.setCreateDate(LocalDateTime.now());


        CERTIFICATE_DTO_RESP = new CertificateDTOResp(
                CERTIFICATE_ID,
                CERTIFICATE_NAME,
                TAG_NAMES_LIST,
                CERTIFICATE_DESCRIPTION,
                CERTIFICATE_PRICE,
                CERTIFICATE_DURATION
        );

        SECOND_CERTIFICATE_DTO_RESP = new CertificateDTOResp(
                SECOND_CERTIFICATE_ID,
                SECOND_CERTIFICATE_NAME,
                SECOND_TAG_NAMES_LIST,
                SECOND_CERTIFICATE_DESCRIPTION,
                SECOND_CERTIFICATE_PRICE,
                SECOND_CERTIFICATE_DURATION
        );

        CERTIFICATES = new ArrayList<>();
        CERTIFICATES.add(CERTIFICATE);
        CERTIFICATES.add(SECOND_CERTIFICATE);

        CERTIFICATES_DTO_RESP = List.of(CERTIFICATE_DTO_RESP, SECOND_CERTIFICATE_DTO_RESP);
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
    private CertificatesServiceImpl certificatesService;


    @Test
    void testGetByIdSuccess() {
        when(certificateDAO.getById(anyInt())).thenReturn(Optional.of(CERTIFICATE));
        when(tagsService.getAllNamesByCertificateId(anyInt())).thenReturn(TAG_NAMES_LIST);
        when(certificateMapper.toDTO(any(Certificate.class), anyList())).thenReturn(CERTIFICATE_DTO_RESP);

        assertEquals(CERTIFICATE_DTO_RESP, certificatesService.getById(CERTIFICATE_ID));
        verify(certificateDAO).getById(CERTIFICATE_ID);
        verify(tagsService).getAllNamesByCertificateId(CERTIFICATE_ID);
        verify(certificateMapper).toDTO(CERTIFICATE, TAG_NAMES_LIST);
    }

    @Test
    void testGetByIdWhenCertificateNotFound() {
        when(certificateDAO.getById(anyInt())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificatesService.getById(CERTIFICATE_ID));
        verify(certificateDAO).getById(CERTIFICATE_ID);
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
    }

    @Test
    void testGetCertificatesSuccessAllArgumentsAreNull() {
        when(certificateDAO.getAll()).thenReturn(Optional.of(CERTIFICATES));
        when(tagsService.getAllNamesByCertificateId(anyInt()))
                .thenReturn(TAG_NAMES_LIST)
                .thenReturn(SECOND_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(any(Certificate.class), anyList()))
                .thenReturn(CERTIFICATE_DTO_RESP)
                .thenReturn(SECOND_CERTIFICATE_DTO_RESP);

        List<CertificateDTOResp> actualResult = certificatesService
                .getCertificates(null, null, null, null);

        assertEquals(CERTIFICATES_DTO_RESP, actualResult);
        verify(certificateDAO, times(1)).getAll();
        verify(tagsService, times(1)).getAllNamesByCertificateId(CERTIFICATE_ID);
        verify(tagsService, times(1)).getAllNamesByCertificateId(SECOND_CERTIFICATE_ID);
        verify(certificateMapper, times(1)).toDTO(CERTIFICATE, TAG_NAMES_LIST);
        verify(certificateMapper, times(1)).toDTO(SECOND_CERTIFICATE, SECOND_TAG_NAMES_LIST);
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
        when(certificateDAO.getCertificatesByTagName(TAG_NAME)).thenReturn(Optional.of(CERTIFICATES));
        when(tagsService.getAllNamesByCertificateId(anyInt()))
                .thenReturn(TAG_NAMES_LIST)
                .thenReturn(SECOND_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(any(Certificate.class), anyList()))
                .thenReturn(CERTIFICATE_DTO_RESP)
                .thenReturn(SECOND_CERTIFICATE_DTO_RESP);

        List<CertificateDTOResp> actualResult = certificatesService
                .getCertificates(TAG_NAME, null, null, null);

        assertEquals(CERTIFICATES_DTO_RESP, actualResult);
        verify(certificateDAO, times(1)).getCertificatesByTagName(TAG_NAME);
        verify(tagsService, times(1)).getAllNamesByCertificateId(CERTIFICATE_ID);
        verify(tagsService, times(1)).getAllNamesByCertificateId(SECOND_CERTIFICATE_ID);
        verify(certificateMapper, times(1)).toDTO(CERTIFICATE, TAG_NAMES_LIST);
        verify(certificateMapper, times(1)).toDTO(SECOND_CERTIFICATE, SECOND_TAG_NAMES_LIST);
        verify(certificateDAO, never()).getAll();
    }

    @Test
    void testGetCertificatesByTagNameThrowsEntityNotFoundException() {
        when(certificateDAO.getCertificatesByTagName(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificatesService
                .getCertificates(TAG_NAME, null, null, null));
        verify(certificateDAO, times(1)).getCertificatesByTagName(TAG_NAME);
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO, never()).getAll();
    }

    @Test
    void testGetCertificatesByPartOfNameOrDescriptionSuccess() {
        when(certificateDAO.getAll()).thenReturn(Optional.of(CERTIFICATES));
        when(tagsService.getAllNamesByCertificateId(anyInt()))
                .thenReturn(TAG_NAMES_LIST)
                .thenReturn(SECOND_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(any(Certificate.class), anyList()))
                .thenReturn(CERTIFICATE_DTO_RESP)
                .thenReturn(SECOND_CERTIFICATE_DTO_RESP);

        List<CertificateDTOResp> actualResult = certificatesService
                .getCertificates(
                        null,
                        PART_OF_DESCRIPTION,
                        null,
                        null
                );

        assertEquals(List.of(CERTIFICATE_DTO_RESP), actualResult);
        verify(certificateDAO, times(1)).getAll();
        verify(tagsService, times(1)).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, times(1)).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO, never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesByPartOfNameOrDescriptionThrowsEntityNotFoundException() {
        when(certificateDAO.getAll()).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificatesService
                .getCertificates(
                        null,
                        PART_OF_DESCRIPTION + " fake",
                        null,
                        null
                )
        );
        verify(certificateDAO, times(1)).getAll();
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO, never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesWithSortingSeparatelyByNameAndDateSuccess() {
        List<CertificateDTOResp> reversedCertDTOResp = List.of(SECOND_CERTIFICATE_DTO_RESP, CERTIFICATE_DTO_RESP);
        List<CertificateDTOResp> actualResult;

        when(certificateDAO.getAll()).thenReturn(Optional.of(CERTIFICATES));
        when(tagsService.getAllNamesByCertificateId(CERTIFICATE_ID)).thenReturn(TAG_NAMES_LIST);
        when(tagsService.getAllNamesByCertificateId(SECOND_CERTIFICATE_ID)).thenReturn(SECOND_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(CERTIFICATE, TAG_NAMES_LIST)).thenReturn(CERTIFICATE_DTO_RESP);
        when(certificateMapper.toDTO(SECOND_CERTIFICATE, SECOND_TAG_NAMES_LIST)).thenReturn(SECOND_CERTIFICATE_DTO_RESP);

        // Sorting by ID (enabled by default)
        actualResult = certificatesService.getCertificates(null, null, null, null);
        assertEquals(CERTIFICATES_DTO_RESP, actualResult);

        // Sorting by name
        actualResult = certificatesService.getCertificates(null, null, "name", null);
        assertEquals(reversedCertDTOResp, actualResult);

        // Sorting by date
        actualResult = certificatesService.getCertificates(null, null, "date", null);
        assertEquals(reversedCertDTOResp, actualResult);

        verify(certificateDAO, times(3)).getAll();
        verify(tagsService, times(3)).getAllNamesByCertificateId(CERTIFICATE_ID);
        verify(tagsService, times(3)).getAllNamesByCertificateId(SECOND_CERTIFICATE_ID);
        verify(certificateMapper, times(3)).toDTO(CERTIFICATE, TAG_NAMES_LIST);
        verify(certificateMapper, times(3)).toDTO(SECOND_CERTIFICATE, SECOND_TAG_NAMES_LIST);
        verify(certificateDAO, never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesWithSortingByThrowsUnsupportedSortingParameterException() {
        when(certificateDAO.getAll()).thenReturn(Optional.of(CERTIFICATES));

        assertThrows(UnsupportedSortingParameter.class, () -> certificatesService
                .getCertificates(null, null, "invalidParameter", null));

        verify(certificateDAO, times(1)).getAll();
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO, never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesWithOrderingSeparatelyByASCAndDESCSuccess() {
        List<CertificateDTOResp> reversedCertDTOResp = List.of(SECOND_CERTIFICATE_DTO_RESP, CERTIFICATE_DTO_RESP);
        List<CertificateDTOResp> actualResult;

        when(certificateDAO.getAll()).thenReturn(Optional.of(CERTIFICATES));
        when(tagsService.getAllNamesByCertificateId(CERTIFICATE_ID)).thenReturn(TAG_NAMES_LIST);
        when(tagsService.getAllNamesByCertificateId(SECOND_CERTIFICATE_ID)).thenReturn(SECOND_TAG_NAMES_LIST);
        when(certificateMapper.toDTO(CERTIFICATE, TAG_NAMES_LIST)).thenReturn(CERTIFICATE_DTO_RESP);
        when(certificateMapper.toDTO(SECOND_CERTIFICATE, SECOND_TAG_NAMES_LIST)).thenReturn(SECOND_CERTIFICATE_DTO_RESP);

        // Sorting by ID ASC order
        actualResult = certificatesService.getCertificates(null, null, null, "asc");
        assertEquals(CERTIFICATES_DTO_RESP, actualResult);

        // Sorting by ID DESC order
        actualResult = certificatesService.getCertificates(null, null, null, "desc");
        assertEquals(reversedCertDTOResp, actualResult);

        verify(certificateDAO, times(2)).getAll();
        verify(tagsService, times(2)).getAllNamesByCertificateId(CERTIFICATE_ID);
        verify(tagsService, times(2)).getAllNamesByCertificateId(SECOND_CERTIFICATE_ID);
        verify(certificateMapper, times(2)).toDTO(CERTIFICATE, TAG_NAMES_LIST);
        verify(certificateMapper, times(2)).toDTO(SECOND_CERTIFICATE, SECOND_TAG_NAMES_LIST);
        verify(certificateDAO, never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testGetCertificatesWithOrderingSeparatelyByASCAndDESCThrowsUnsupportedSortingParameterException() {
        when(certificateDAO.getAll()).thenReturn(Optional.of(CERTIFICATES));

        assertThrows(UnsupportedSortingParameter.class, () -> certificatesService
                .getCertificates(null, null, null, "invalidOrder"));

        verify(certificateDAO, times(1)).getAll();
        verify(tagsService, never()).getAllNamesByCertificateId(anyInt());
        verify(certificateMapper, never()).toDTO(any(Certificate.class), anyList());
        verify(certificateDAO, never()).getCertificatesByTagName(anyString());
    }

    @Test
    void testUpdateSuccess() {
        int updateCertificateId = CERTIFICATE_ID;
        List<String> newTags = SECOND_TAG_NAMES_LIST;
        List<String> oldTags = TAG_NAMES_LIST;

        CertificateDTOReq certDTOReq = new CertificateDTOReq(
                SECOND_CERTIFICATE_NAME,
                newTags,
                SECOND_CERTIFICATE_DESCRIPTION,
                SECOND_CERTIFICATE_PRICE,
                SECOND_CERTIFICATE_DURATION
        );

        Certificate oldCertificate = new Certificate();
        oldCertificate.setId(CERTIFICATE_ID);
        oldCertificate.setName(CERTIFICATE_NAME);
        oldCertificate.setDescription(CERTIFICATE_DESCRIPTION);
        oldCertificate.setPrice(CERTIFICATE_PRICE);
        oldCertificate.setDuration(CERTIFICATE_DURATION);

        CertificateDTOResp oldCertDTOResp = new CertificateDTOResp(
                CERTIFICATE_ID,
                CERTIFICATE_NAME,
                oldTags,
                CERTIFICATE_DESCRIPTION,
                CERTIFICATE_PRICE,
                CERTIFICATE_DURATION
        );

        Certificate newCertificate = new Certificate();
        newCertificate.setId(CERTIFICATE_ID);
        newCertificate.setName(SECOND_CERTIFICATE_NAME);
        newCertificate.setDescription(SECOND_CERTIFICATE_DESCRIPTION);
        newCertificate.setPrice(SECOND_CERTIFICATE_PRICE);
        newCertificate.setDuration(SECOND_CERTIFICATE_DURATION);

        CertificateDTOResp expectedCertDTOResp = new CertificateDTOResp(
                CERTIFICATE_ID,
                SECOND_CERTIFICATE_NAME,
                newTags,
                SECOND_CERTIFICATE_DESCRIPTION,
                SECOND_CERTIFICATE_PRICE,
                SECOND_CERTIFICATE_DURATION
        );

        when(certificateDAO.getById(updateCertificateId))
                .thenReturn(Optional.of(oldCertificate))
                .thenReturn(Optional.of(newCertificate));
        when(tagsService.getAllNamesByCertificateId(updateCertificateId))
                .thenReturn(oldTags)
                .thenReturn(newTags);
        when(certificateMapper.toDTO(any(Certificate.class), anyList()))
                .thenReturn(oldCertDTOResp)
                .thenReturn(expectedCertDTOResp);
        when(tagsService.getTagIdByName("Tag2")).thenReturn(2);
        when(tagsService.getTagIdByName("Tag3")).thenReturn(3);
        when(tagsService.getTagIdByName("Tag4")).thenReturn(4);
        when(tagsService.getTagIdByName("Tag5")).thenReturn(5);
        when(certificateMapper.toUpdatedEntity(certDTOReq, oldCertDTOResp)).thenReturn(newCertificate);

        CertificateDTOResp actualCertDTOResp = certificatesService.update(updateCertificateId, certDTOReq);

        assertEquals(expectedCertDTOResp, actualCertDTOResp);
        verify(certificateDAO).update(updateCertificateId, newCertificate);
        verify(certificatesTagsService).deleteTagFromCertificate(2, updateCertificateId);
        verify(certificatesTagsService).deleteTagFromCertificate(3, updateCertificateId);
        verify(certificatesTagsService).attachTagToCertificate(4, updateCertificateId);
        verify(certificatesTagsService).attachTagToCertificate(5, updateCertificateId);
    }

    @Test
    void testUpdateEntityNotFoundException() {
        int updateCertificateId = 1;

        CertificateDTOReq certDTOReq = new CertificateDTOReq(
                "Updated Name",
                null,
                "Updated Description",
                BigDecimal.valueOf(150.00),
                15
        );

        when(certificateDAO.getById(updateCertificateId)).thenReturn(Optional.empty());

        verify(certificateDAO, never()).update(anyInt(), any(Certificate.class));
        verify(certificatesTagsService, never()).deleteTagFromCertificate(anyInt(), anyInt());
        verify(certificatesTagsService, never()).attachTagToCertificate(anyInt(), anyInt());
        assertThrows(EntityNotFoundException.class, () -> certificatesService.update(updateCertificateId, certDTOReq));
    }

    @Test
    void testDeleteByIdSuccess() {
        when(certificateDAO.deleteById(anyInt())).thenReturn(1);

        certificatesService.deleteById(CERTIFICATE_ID);

        verify(certificateDAO).deleteById(CERTIFICATE_ID);
    }

    @Test
    void testDeleteByIdThrowsEntityNotDeletedException() {
        when(certificateDAO.deleteById(anyInt())).thenReturn(0);

        assertThrows(EntityNotDeletedException.class, () -> certificatesService.deleteById(CERTIFICATE_ID));
        verify(certificateDAO).deleteById(CERTIFICATE_ID);
    }
}
