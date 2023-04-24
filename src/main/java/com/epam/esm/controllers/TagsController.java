package com.epam.esm.controllers;

import com.epam.esm.models.dtos.tag.TagDTOReq;
import com.epam.esm.models.dtos.tag.TagDTOResp;
import com.epam.esm.services.TagsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Myroslav Dudnyk
 */
@RestController
@RequestMapping(value = "/tags", produces = APPLICATION_JSON_VALUE)
public class TagsController {
    private final TagsService tagsService;

    public TagsController(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDTOResp> getById(@PathVariable("id") int id) {
        return ResponseEntity.ok(tagsService.getById(id));
    }

    @GetMapping()
    public ResponseEntity<List<TagDTOResp>> getAll() {
        return ResponseEntity.ok(tagsService.getAll());
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<TagDTOResp> create(@RequestBody TagDTOReq tagDTOReq) {
        TagDTOResp createdTag = tagsService.create(tagDTOReq);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(createdTag.id())
                .toUri();

        return ResponseEntity.created(location).body(createdTag);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        tagsService.deleteById(id);
    }
}
