package com.epam.esm.dao;

import com.epam.esm.models.Certificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

/**
 * @author Myroslav Dudnyk
 */
@Component
public class CertificateDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CertificateDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int create(Certificate certificate) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String sql =
                "INSERT INTO gift_certificate (name, description, price, duration) VALUES (?, ?, ?, ?) RETURNING id";

        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, certificate.getName());
            preparedStatement.setString(2, certificate.getDescription());
            preparedStatement.setBigDecimal(3, certificate.getPrice());
            preparedStatement.setInt(4, certificate.getDuration());

            return preparedStatement;
        }, generatedKeyHolder);

        return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
    }

    public List<Certificate> getAll() {
        return jdbcTemplate
                .query("SELECT * FROM gift_certificate ORDER BY id", new CertificateMapper());
    }

    public Certificate getById(int id) {
        return jdbcTemplate
                .query("SELECT * FROM gift_certificate WHERE id=?",
                        new CertificateMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void delete(int id) {
        jdbcTemplate
                .update("DELETE FROM gift_certificate WHERE id=?", id);
    }
}
