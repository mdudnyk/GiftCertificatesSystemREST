package com.epam.esm.dao;

import com.epam.esm.models.Tag;
import com.epam.esm.models.TagDTO;
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
public class TagDAO {
    private static final String CREATE_TAG = "INSERT INTO tag (name) VALUES (?)";

    private static final String GET_ALL_TAGS = "SELECT * FROM tag ORDER BY id";

    private static final String GET_TAG_BY_ID = "SELECT * FROM tag WHERE id=?";

    private static final String DELETE_TAG = "DELETE FROM tag WHERE id=?";

    private final JdbcTemplate jdbcTemplate;

    public TagDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int create(TagDTO tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_TAG, new String[]{"id"});
            ps.setString(1, tag.getName());
            return ps;
        }, keyHolder);

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new NullPointerException("Generated key 'id' for tag with name='" + tag.getName() + "' equals null");
        }

        return generatedId.intValue();
    }

    public List<Tag> getAll() {
        return jdbcTemplate
                .query(GET_ALL_TAGS, new TagMapper());
    }

    public Tag getById(int id) {
        return jdbcTemplate
                .query(GET_TAG_BY_ID, new TagMapper(), id)
                .stream().findFirst().orElseThrow(() ->
                        new EntityNotFoundException("Requested tag not found (id = " + id + ")"));
    }

    public void deleteById(int id) {
        int deletedRows = jdbcTemplate.update(DELETE_TAG, id);
        if (deletedRows == 0) {
            throw new EntityNotFoundException("Unable to delete tag with id=" + id + ". It was not found");
        }
    }
}
