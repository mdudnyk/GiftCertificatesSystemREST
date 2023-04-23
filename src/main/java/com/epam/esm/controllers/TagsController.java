package com.epam.esm.controllers;

import com.epam.esm.services.impl.TagsServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author Myroslav Dudnyk
 */
@RestController
@RequestMapping(value = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagsController {
    private final TagsServiceImpl tagsServiceImpl;

    public TagsController(TagsServiceImpl tagsServiceImpl) {
        this.tagsServiceImpl = tagsServiceImpl;
    }

//    @GetMapping()
//    @ResponseStatus(HttpStatus.OK)
//    public List<Tag> getAll() {
//        return tagsServiceImpl.getAll();
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Tag> getById(@PathVariable("id") int id) {
//        Tag tag = tagsServiceImpl.getById(id);
//        return ResponseEntity.ok(tag);
//    }

//    @PostMapping(consumes = "application/json", produces = "application/json")
//    public ResponseEntity<Tag> create(@RequestBody TagDTO tag, HttpServletRequest request) {
//        Tag createdTag = tagsServiceImpl.create(tag);
//
//        URI location = ServletUriComponentsBuilder.fromRequestUri(request)
//                .path("/{id}")
//                .buildAndExpand(createdTag.getId())
//                .toUri();
//
//        return ResponseEntity.created(location).body(createdTag);
//    }

//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void delete(@PathVariable("id") int id) {
//        tagsServiceImpl.deleteById(id);
//    }
}