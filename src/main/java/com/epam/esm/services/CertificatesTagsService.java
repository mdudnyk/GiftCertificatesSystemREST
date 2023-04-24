package com.epam.esm.services;

/**
 * @author Myroslav Dudnyk
 */
public interface CertificatesTagsService {
    void attachTagToCertificate(int tagId, int certificateId);

    void deleteTagFromCertificate(int tagId, int certificateId);
}
