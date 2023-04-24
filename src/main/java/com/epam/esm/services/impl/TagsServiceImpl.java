package com.epam.esm.services.impl;

import com.epam.esm.dao.TagDAO;
import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.tag.TagDTOReq;
import com.epam.esm.models.dtos.tag.TagDTOResp;
import com.epam.esm.services.TagsService;
import com.epam.esm.services.exceptions.EntityAlreadyExistsException;
import com.epam.esm.services.exceptions.EntityNotDeletedException;
import com.epam.esm.services.exceptions.EntityNotFoundException;
import com.epam.esm.services.exceptions.ServiceException;
import com.epam.esm.services.mappers.tag.TagMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Myroslav Dudnyk
 */
@Service
public class TagsServiceImpl implements TagsService {
    private final static String ENTITY_NAME = "tag";

    private final TagMapper tagMapper;

    private final TagDAO tagDAO;

    public TagsServiceImpl(TagMapper tagMapper, TagDAO tagDAO) {
        this.tagMapper = tagMapper;
        this.tagDAO = tagDAO;
    }

    @Override
    public TagDTOResp getById(int id) {
        Tag tag = tagDAO.getById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));

        return tagMapper.toDTO(tag);
    }

    @Override
    public List<String> getAllNamesByCertificateId(int certificateId) {
        return tagDAO.getTagsByCertificateId(certificateId)
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    @Override
    public int getTagIdByName(String tagName) {
        return tagDAO.getTagIdByName(tagName)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, tagName));
    }

    @Override
    public List<TagDTOResp> getAll() {
        List<Tag> tags = tagDAO.getAll()
                .orElseThrow(() -> new EntityNotFoundException("Tags not found"));

        return tags.stream()
                .map(tagMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TagDTOResp create(TagDTOReq tagDTOReq) {
        if (tagDAO.checkIfTagWithNameExists(tagDTOReq.name())) {
            throw new EntityAlreadyExistsException(ENTITY_NAME, tagDTOReq.name());
        }

        int newTagId = (int) tagDAO.create(tagMapper.toEntity(tagDTOReq))
                .orElseThrow(() ->
                        new ServiceException("Unable to get an ID of created tag", 40024));

        return getById(newTagId);
    }

    @Override
    public void deleteById(int id) {
        int deletedRows = tagDAO.deleteById(id);

        if (deletedRows == 0) {
            throw new EntityNotDeletedException(ENTITY_NAME, id);
        }
    }
}
