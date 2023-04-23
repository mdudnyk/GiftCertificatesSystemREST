package com.epam.esm.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Myroslav Dudnyk
 */
@Component
public class CertificateTagDAO {
    private static final String CREATE_CERTIFICATE_TAG = "INSERT INTO gift_certificates_tags (certificate_id, tag_id) VALUES (?, ?)";

//    private static final String DELETE_BY_CERTIFICATE_ID_AND_TAG_ID = "DELETE FROM gift_certificates_tags " +
//            "WHERE certificate_id=? AND tag_id=?";

    private final JdbcTemplate jdbcTemplate;

    public CertificateTagDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int create(int certificateId, int tagId) {
        return jdbcTemplate.update(CREATE_CERTIFICATE_TAG, certificateId, tagId);
    }

//    public void deleteByCertificateIdAndTagId(int certificateId, int tagId) {
//        int deletedRows = jdbcTemplate.update(DELETE_BY_CERTIFICATE_ID_AND_TAG_ID, certificateId, tagId);
//        if (deletedRows == 0) {
//            throw new EntityNotFoundException("Unable to delete connection between certificate with id="
//                    + certificateId + " and tag with id=" + tagId + ". It was not found");
//        }
//    }
}
