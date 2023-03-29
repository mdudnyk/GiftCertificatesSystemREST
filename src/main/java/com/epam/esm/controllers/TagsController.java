package com.epam.esm.controllers;

import com.epam.esm.dao.TagDAO;
import com.epam.esm.models.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@RestController
@RequestMapping(value = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagsController {
    private final TagDAO tagDAO;

    @Autowired
    public TagsController(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    }

    @GetMapping()
    public List<Tag> allTags() {
        return tagDAO.getAll();
    }

    @GetMapping("/{id}")
    public Tag getById(@PathVariable("id") int id) {
        return tagDAO.getById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Tag newTag(@RequestBody Tag tag) {
        int newTagId = tagDAO.create(tag);
        return tagDAO.getById(newTagId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        tagDAO.delete(id);
    }
}
