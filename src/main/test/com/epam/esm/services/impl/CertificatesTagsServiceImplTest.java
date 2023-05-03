package com.epam.esm.services.impl;

import com.epam.esm.dao.CertificateTagDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

/**
 * @author Myroslav Dudnyk
 */
@ExtendWith(MockitoExtension.class)
class CertificatesTagsServiceImplTest {
    private static final int TEST_TAG_ID = 1;
    private static final int TEST_CERTIFICATE_ID = 1;

    @Mock
    private CertificateTagDAO certificateTagDAO;

    @InjectMocks
    private CertificatesTagsServiceImpl certificatesTagsService;

    @Test
    public void testAttachTagToCertificate() {
        certificatesTagsService.attachTagToCertificate(TEST_TAG_ID, TEST_CERTIFICATE_ID);

        verify(certificateTagDAO).create(TEST_CERTIFICATE_ID, TEST_TAG_ID);
    }

    @Test
    public void testDeleteTagFromCertificate() {
        certificatesTagsService.deleteTagFromCertificate(TEST_TAG_ID, TEST_CERTIFICATE_ID);

        verify(certificateTagDAO).deleteByCertificateIdAndTagId(TEST_CERTIFICATE_ID, TEST_TAG_ID);
    }
}
