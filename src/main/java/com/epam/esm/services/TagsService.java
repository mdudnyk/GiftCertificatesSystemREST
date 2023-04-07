package com.epam.esm.services;

import com.epam.esm.dao.TagDAO;
import com.epam.esm.models.Tag;
import com.epam.esm.models.TagDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@Service
public class TagsService {
    private final TagDAO tagDAO;

    public TagsService(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    }

    public List<Tag> getAll() {
        return tagDAO.getAll();
    }

    public Tag getById(int id) {
        return tagDAO.getById(id);
    }

    public Tag create(TagDTO tag) {
        int newTagId = tagDAO.create(tag);
        return getById(newTagId);
    }

    public void deleteById(int id) {
        tagDAO.deleteById(id);
    }
}