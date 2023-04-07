package com.epam.esm.dao;

import com.epam.esm.models.Certificate;
import com.epam.esm.models.CertificateDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@Component
public class CertificateDAO {
    private static final String CREATE_CERTIFICATE =
            "INSERT INTO gift_certificate (name, description, price, duration) VALUES (?, ?, ?, ?)";

    private static final String GET_ALL_CERTIFICATES = "SELECT * FROM gift_certificate ORDER BY id";

    private static final String GET_CERTIFICATE_BY_ID = "SELECT * FROM gift_certificate WHERE id=?";

    private static final String DELETE_CERTIFICATE = "DELETE FROM gift_certificate WHERE id=?";

    private final JdbcTemplate jdbcTemplate;

    public CertificateDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int create(CertificateDTO certificate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_CERTIFICATE, new String[]{"id"});
            ps.setString(1, certificate.getName());
            ps.setString(2, certificate.getDescription());
            ps.setBigDecimal(3, certificate.getPrice());
            ps.setInt(4, certificate.getDuration());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new NullPointerException("Generated key 'id' for certificate with name='"
                    + certificate.getName() + "' equals null");
        }

        return generatedId.intValue();
    }

    public List<Certificate> getAll() {
        return jdbcTemplate
                .query(GET_ALL_CERTIFICATES, new CertificateMapper());
    }

    public Certificate getById(int id) {
        return jdbcTemplate
                .query(GET_CERTIFICATE_BY_ID, new CertificateMapper(), id)
                .stream().findFirst().orElseThrow(() ->
                        new EntityNotFoundException("Requested certificate not found (id = " + id + ")"));
    }

    public void deleteById(int id) {
        int deletedRows = jdbcTemplate.update(DELETE_CERTIFICATE, id);
        if (deletedRows == 0) {
            throw new EntityNotFoundException("Unable to delete certificate with id=" + id + ". It was not found");
        }
    }
}