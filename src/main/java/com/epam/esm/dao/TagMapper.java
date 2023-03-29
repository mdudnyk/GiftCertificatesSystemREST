package com.epam.esm.dao;

import com.epam.esm.models.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Myroslav Dudnyk
 */
public class TagMapper implements RowMapper<Tag> {
    @Override
    public Tag mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        Tag tag = new Tag();

        tag.setId(rs.getInt("id"));
        tag.setName(rs.getString("name"));

        return tag;
    }
}
