package com.epam.esm.dao;

import com.epam.esm.dao.mappers.CertificateDAOMapper;
import com.epam.esm.models.Certificate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

/**
 * @author Myroslav Dudnyk
 */
@Component
public class CertificateDAO {
    private static final String GET_CERTIFICATE_BY_ID = "SELECT * FROM gift_certificate WHERE id = ?";

    private static final String COUNT_CERTIFICATES_WITH_SPECIFIED_NAME = "SELECT COUNT(*) FROM gift_certificate WHERE name = ?";

    private static final String GET_ALL_CERTIFICATES = "SELECT * FROM gift_certificate";

    private static final String GET_CERTIFICATES_BY_TAG_NAME = "SELECT gc.* FROM gift_certificate gc " +
            "JOIN gift_certificates_tags gct ON gc.id = gct.certificate_id " +
            "JOIN tag t ON gct.tag_id = t.id " +
            "WHERE t.name = ?";

    private static final String CREATE_CERTIFICATE =
            "INSERT INTO gift_certificate (name, description, price, duration) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_CERTIFICATE_BY_ID = "UPDATE gift_certificate SET name = ?, description = ?, " +
            "price = ?, duration = ? WHERE id = ?";

    private static final String DELETE_CERTIFICATE = "DELETE FROM gift_certificate WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;


    public CertificateDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public Optional<Certificate> getById(int id) {
        return jdbcTemplate.query(GET_CERTIFICATE_BY_ID, new CertificateDAOMapper(), id)
                .stream()
                .findFirst();
    }

    public boolean checkIfCertificateWithNameExists(String name) {
        Integer count = jdbcTemplate
                .queryForObject(COUNT_CERTIFICATES_WITH_SPECIFIED_NAME, Integer.class, name);

        return count != null && count > 0;
    }

    public Optional<List<Certificate>> getAll() {
        List<Certificate> resultList = jdbcTemplate
                .query(GET_ALL_CERTIFICATES, new CertificateDAOMapper());

        return resultList.size() > 0 ? Optional.of(resultList) : Optional.empty();
    }

    public Optional<List<Certificate>> getCertificatesByTagName(String tagName) {
        List<Certificate> resultList = jdbcTemplate
                .query(GET_CERTIFICATES_BY_TAG_NAME, new CertificateDAOMapper(), tagName);

        return resultList.size() > 0 ? Optional.of(resultList) : Optional.empty();
    }

    public Optional<Number> create(Certificate certificate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_CERTIFICATE, new String[]{"id"});
            ps.setString(1, certificate.getName());
            ps.setString(2, certificate.getDescription());
            ps.setBigDecimal(3, certificate.getPrice());
            ps.setInt(4, certificate.getDuration());
            return ps;
        }, keyHolder);

        return Optional.ofNullable(keyHolder.getKey());
    }

    public int update(int certificateId, Certificate certificate) {
        return jdbcTemplate.update(UPDATE_CERTIFICATE_BY_ID,
                certificate.getName(),
                certificate.getDescription(),
                certificate.getPrice(),
                certificate.getDuration(),
                certificateId);
    }

    public int deleteById(int id) {
        return jdbcTemplate.update(DELETE_CERTIFICATE, id);
    }
}
