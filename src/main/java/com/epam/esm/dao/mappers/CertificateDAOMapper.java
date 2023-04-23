package com.epam.esm.dao.mappers;


import com.epam.esm.models.Certificate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Myroslav Dudnyk
 */
public class CertificateDAOMapper implements RowMapper<Certificate> {
    @Override
    public Certificate mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        Certificate certificate = new Certificate();

        certificate.setId(rs.getInt("id"));
        certificate.setName(rs.getString("name"));
        certificate.setDescription(rs.getString("description"));
        certificate.setPrice(rs.getBigDecimal("price"));
        certificate.setDuration(rs.getInt("duration"));
        certificate.setCreateDate(rs.getTimestamp("create_date")
                .toLocalDateTime());
        certificate.setLastUpdateDate(rs.getTimestamp("last_update_date")
                .toLocalDateTime());

        return certificate;
    }
}
