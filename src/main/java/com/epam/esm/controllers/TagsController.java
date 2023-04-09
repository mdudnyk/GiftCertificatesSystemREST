package com.epam.esm.controllers;

import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.TagDTO;
import com.epam.esm.services.TagsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
@RestController
@RequestMapping(value = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagsController {
    private final TagsService tagsService;

    public TagsController(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<Tag> getAll() {
        return tagsService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getById(@PathVariable("id") int id) {
        Tag tag = tagsService.getById(id);
        return ResponseEntity.ok(tag);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Tag> create(@RequestBody TagDTO tag, HttpServletRequest request) {
        Tag createdTag = tagsService.create(tag);

        URI location = ServletUriComponentsBuilder.fromRequestUri(request)
                .path("/{id}")
                .buildAndExpand(createdTag.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdTag);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        tagsService.deleteById(id);
    }
}