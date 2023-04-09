package com.epam.esm.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@Component
public class CertificateTagDAO {
    private static final String CREATE_CERTIFICATE_TAG = "INSERT INTO gift_certificates_tags (certificate_id, tag_id) VALUES (?, ?)";

//    private static final String GET_CERTIFICATES_ID_BY_TAG_ID = "SELECT certificate_id FROM gift_certificates_tags WHERE tag_id=?";
//
//    private static final String GET_TAGS_ID_BY_CERTIFICATE_ID = "SELECT tag_id FROM gift_certificates_tags WHERE certificate_id=?";
//
//    private static final String DELETE_CERTIFICATE_TAG = "DELETE FROM gift_certificates_tags WHERE certificate_id=?, tag_id=?";

    private final JdbcTemplate jdbcTemplate;

    public CertificateTagDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int create(int certificateId, int tagId) {
        return jdbcTemplate.update(CREATE_CERTIFICATE_TAG, certificateId, tagId);
    }

//    public List<Integer> getTagsIdByCertificateId(int certificateId) {
//        return jdbcTemplate
//                .query(GET_TAGS_ID_BY_CERTIFICATE_ID,
//                        (rs, rowNum) -> rs.getInt("tag_id"),
//                        certificateId);
//    }
}
