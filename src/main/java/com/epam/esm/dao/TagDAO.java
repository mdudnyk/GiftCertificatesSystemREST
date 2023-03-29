package com.epam.esm.dao;

import com.epam.esm.models.Tag;
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
public class TagDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int create(Tag tag) {
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String sql =
                "INSERT INTO tag (name) VALUES (?) RETURNING id";

        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, tag.getName());

            return preparedStatement;
        }, generatedKeyHolder);

        return Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
    }

    public List<Tag> getAll() {
        return jdbcTemplate
                .query("SELECT * FROM tag ORDER BY id", new TagMapper());
    }

    public Tag getById(int id) {
        return jdbcTemplate
                .query("SELECT * FROM tag WHERE id=?",
                        new TagMapper(), id)
                .stream().findAny().orElse(null);
    }

    public void delete(int id) {
        jdbcTemplate
                .update("DELETE FROM tag WHERE id=?", id);
    }
}
