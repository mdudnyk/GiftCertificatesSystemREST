package com.epam.esm.dao;

import com.epam.esm.dao.mappers.TagDAOMapper;
import com.epam.esm.models.Tag;
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
public class TagDAO {
    private static final String CREATE_TAG = "INSERT INTO tag (name) VALUES (?)";

//    private static final String GET_ALL_TAGS = "SELECT * FROM tag ORDER BY id";

    private static final String GET_TAGS_BY_CERTIFICATE_ID = "SELECT id, name FROM tag " +
            "INNER JOIN gift_certificates_tags gct ON tag.id = gct.tag_id " +
            "WHERE certificate_id = ? ORDER BY tag_id";

    private static final String GET_TAG_BY_ID = "SELECT * FROM tag WHERE id = ?";

    private static final String GET_TAG_ID_BY_NAME = "SELECT id FROM tag WHERE name = ?";

//    private static final String DELETE_TAG = "DELETE FROM tag WHERE id = ?";

    private final JdbcTemplate jdbcTemplate;

    public TagDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Tag> getById(int id) {
        return jdbcTemplate
                .query(GET_TAG_BY_ID, new TagDAOMapper(), id)
                .stream().findFirst();
    }

    public Optional<Integer> getTagIdByName(String name) {
        return jdbcTemplate.query(GET_TAG_ID_BY_NAME, (rs, rowNum) -> rs.getInt("id"), name)
                .stream()
                .findFirst();
    }

//    public List<Tag> getAll() {
//        return jdbcTemplate
//                .query(GET_ALL_TAGS, new TagDAOMapper());
//    }

    public List<Tag> getTagsByCertificateId(int certificateId) {
        return jdbcTemplate
                .query(GET_TAGS_BY_CERTIFICATE_ID, new TagDAOMapper(), certificateId);
    }

    public Optional<Number> create(Tag tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_TAG, new String[]{"id"});
            ps.setString(1, tag.getName());
            return ps;
        }, keyHolder);

        return Optional.ofNullable(keyHolder.getKey());
    }

//    public void deleteById(int id) {
//        int deletedRows = jdbcTemplate.update(DELETE_TAG, id);
//        if (deletedRows == 0) {
//            throw new EntityNotFoundException("Unable to delete tag with id=" + id + ". It was not found");
//        }
//    }
}
