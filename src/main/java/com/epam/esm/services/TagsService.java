package com.epam.esm.services;

import com.epam.esm.dao.TagDAO;
import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.TagDTO;
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

    public List<Tag> getTagsByCertificateId(int certificateId) {
        return tagDAO.getTagsByCertificateId(certificateId);
    }

    public Tag getById(int id) {
        return tagDAO.getById(id);
    }

    public Tag getByName(String name) {
        return tagDAO.getByName(name);
    }

    public Tag create(TagDTO tag) {
        int newTagId = tagDAO.create(tag);
        return getById(newTagId);
    }

    public void deleteById(int id) {
        tagDAO.deleteById(id);
    }
}