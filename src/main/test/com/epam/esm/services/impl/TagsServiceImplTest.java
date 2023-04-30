package com.epam.esm.services.impl;

import com.epam.esm.dao.TagDAO;
import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.tag.TagDTOReq;
import com.epam.esm.models.dtos.tag.TagDTOResp;
import com.epam.esm.services.exceptions.EntityAlreadyExistsException;
import com.epam.esm.services.exceptions.EntityNotDeletedException;
import com.epam.esm.services.exceptions.EntityNotFoundException;
import com.epam.esm.services.exceptions.ServiceException;
import com.epam.esm.services.mappers.tag.TagMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Myroslav Dudnyk
 */
@ExtendWith(MockitoExtension.class)
class TagsServiceImplTest {
    private static final int TEST_TAG_ID = 1;
    private static final String TEST_TAG_NAME = "test_tag_name";
    private static final int TEST_CERTIFICATE_ID = 1;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private TagDAO tagDAO;

    @InjectMocks
    private TagsServiceImpl tagsService;


    @Test
    void testGetById() {
        Tag tag = new Tag();
        tag.setId(TEST_TAG_ID);
        when(tagDAO.getById(TEST_TAG_ID)).thenReturn(Optional.of(tag));

        TagDTOResp tagDTOResp = new TagDTOResp(TEST_TAG_ID, TEST_TAG_NAME);
        when(tagMapper.toDTO(tag)).thenReturn(tagDTOResp);

        TagDTOResp result = tagsService.getById(TEST_TAG_ID);

        assertEquals(tagDTOResp, result);
    }

    @Test
    void testGetByIdThrowsEntityNotFoundException() {
        when(tagDAO.getById(TEST_TAG_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagsService.getById(TEST_TAG_ID));
    }

    @Test
    void testGetAllNamesByCertificateId() {
        Tag tag = new Tag();
        tag.setName(TEST_TAG_NAME);
        List<Tag> tags = singletonList(tag);
        when(tagDAO.getTagsByCertificateId(TEST_CERTIFICATE_ID)).thenReturn(tags);

        List<String> result = tagsService.getAllNamesByCertificateId(TEST_CERTIFICATE_ID);

        assertEquals(singletonList(TEST_TAG_NAME), result);
    }

    @Test
    void testGetTagIdByName() {
        when(tagDAO.getTagIdByName(TEST_TAG_NAME)).thenReturn(Optional.of(TEST_TAG_ID));

        int result = tagsService.getTagIdByName(TEST_TAG_NAME);

        assertEquals(TEST_TAG_ID, result);
    }

    @Test
    void testGetTagIdByNameThrowsEntityNotFoundException() {
        when(tagDAO.getTagIdByName(TEST_TAG_NAME)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagsService.getTagIdByName(TEST_TAG_NAME));
    }

    @Test
    void testGetAll() {
        Tag tag = new Tag();
        List<Tag> tags = singletonList(tag);
        when(tagDAO.getAll()).thenReturn(Optional.of(tags));

        TagDTOResp tagDTOResp = new TagDTOResp(TEST_TAG_ID, TEST_TAG_NAME);
        when(tagMapper.toDTO(tag)).thenReturn(tagDTOResp);

        List<TagDTOResp> result = tagsService.getAll();

        assertEquals(singletonList(tagDTOResp), result);
    }

    @Test
    void testGetAllThrowsEntityNotFoundException() {
        when(tagDAO.getAll()).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagsService.getAll());
    }

    @Test
    void testCreate() {
        TagDTOReq tagDTOReq = new TagDTOReq(TEST_TAG_NAME);

        Tag tag = new Tag();
        tag.setId(TEST_TAG_ID);
        tag.setName(TEST_TAG_NAME);

        when(tagDAO.checkIfTagWithNameExists(TEST_TAG_NAME)).thenReturn(false);
        when(tagDAO.create(any(Tag.class))).thenReturn(Optional.of(TEST_TAG_ID));
        when(tagMapper.toEntity(tagDTOReq)).thenReturn(tag);

        TagDTOResp tagDTOResp = new TagDTOResp(TEST_TAG_ID, TEST_TAG_NAME);
        when(tagDAO.getById(TEST_TAG_ID)).thenReturn(Optional.of(tag));
        when(tagMapper.toDTO(tag)).thenReturn(tagDTOResp);

        TagDTOResp result = tagsService.create(tagDTOReq);

        assertEquals(tagDTOResp, result);
        verify(tagDAO).create(tag);
        verify(tagDAO).getById(TEST_TAG_ID);
        verify(tagDAO).checkIfTagWithNameExists(TEST_TAG_NAME);
    }

    @Test
    void testCreateThrowsEntityAlreadyExistsException() {
        TagDTOReq tagDTOReq = new TagDTOReq(TEST_TAG_NAME);

        when(tagDAO.checkIfTagWithNameExists(TEST_TAG_NAME)).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> tagsService.create(tagDTOReq));
    }

    @Test
    void testCreateThrowsServiceException() {
        TagDTOReq tagDTOReq = new TagDTOReq(TEST_TAG_NAME);

        Tag tag = new Tag();
        when(tagDAO.checkIfTagWithNameExists(TEST_TAG_NAME)).thenReturn(false);
        when(tagDAO.create(any(Tag.class))).thenReturn(Optional.empty());
        when(tagMapper.toEntity(tagDTOReq)).thenReturn(tag);

        assertThrows(ServiceException.class, () -> tagsService.create(tagDTOReq));
    }

    @Test
    void testDeleteById() {
        when(tagDAO.deleteById(TEST_TAG_ID)).thenReturn(1);

        tagsService.deleteById(TEST_TAG_ID);

        verify(tagDAO).deleteById(TEST_TAG_ID);
    }

    @Test
    void testDeleteByIdThrowsEntityNotDeletedException() {
        when(tagDAO.deleteById(TEST_TAG_ID)).thenReturn(0);

        assertThrows(EntityNotDeletedException.class, () -> tagsService.deleteById(TEST_TAG_ID));
    }
}
