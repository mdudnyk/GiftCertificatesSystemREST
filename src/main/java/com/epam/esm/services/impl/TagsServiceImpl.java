package com.epam.esm.services.impl;

import com.epam.esm.dao.TagDAO;
import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.tag.TagDTOReq;
import com.epam.esm.models.dtos.tag.TagDTOResp;
import com.epam.esm.services.TagsService;
import com.epam.esm.services.exceptions.EntityNotFoundException;
import com.epam.esm.services.exceptions.ServiceException;
import com.epam.esm.services.mappers.tag.TagMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Myroslav Dudnyk
 */
@Service
public class TagsServiceImpl implements TagsService {
    private final String ENTITY_NAME = "tag";

    private final TagDAO tagDAO;

    private final TagMapper tagMapper;

    public TagsServiceImpl(final TagDAO tagDAO, final TagMapper tagMapper) {
        this.tagDAO = tagDAO;
        this.tagMapper = tagMapper;
    }

    @Override
    public List<String> getAllNamesByCertificateId(int certificateId) {
        return tagDAO.getTagsByCertificateId(certificateId)
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Integer> getTagIdByName(String name) {
        return tagDAO.getTagIdByName(name);
    }

    @Override
    public TagDTOResp getById(int id) {
        Tag tag = tagDAO.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));

        return tagMapper.toDTO(tag);
    }

    @Override
    public TagDTOResp create(TagDTOReq tagDTOReq) {
        int newTagId = (int) tagDAO.create(tagMapper.toEntity(tagDTOReq))
                .orElseThrow(() ->
                        new ServiceException("Unable to get an ID of created tag", 40024));
        return getById(newTagId);
    }

//    public List<Tag> getAll() {
//        return tagDAO.getAll();
//    }

//    public void deleteById(int id) {
//        tagDAO.deleteById(id);
//    }
}