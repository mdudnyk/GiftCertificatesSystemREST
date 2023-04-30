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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        Mockito.when(tagDAO.getById(TEST_TAG_ID)).thenReturn(Optional.of(tag));

        TagDTOResp tagDTOResp = new TagDTOResp(TEST_TAG_ID, TEST_TAG_NAME);
        Mockito.when(tagMapper.toDTO(tag)).thenReturn(tagDTOResp);

        TagDTOResp result = tagsService.getById(TEST_TAG_ID);

        assertEquals(tagDTOResp, result);
    }

    @Test
    void testGetByIdThrowsEntityNotFoundException() {
        Mockito.when(tagDAO.getById(TEST_TAG_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagsService.getById(TEST_TAG_ID));
    }

    @Test
    void testGetAllNamesByCertificateId() {
        Tag tag = new Tag();
        tag.setName(TEST_TAG_NAME);
        List<Tag> tags = Collections.singletonList(tag);
        Mockito.when(tagDAO.getTagsByCertificateId(TEST_CERTIFICATE_ID)).thenReturn(tags);

        List<String> result = tagsService.getAllNamesByCertificateId(TEST_CERTIFICATE_ID);

        assertEquals(Collections.singletonList(TEST_TAG_NAME), result);
    }

    @Test
    void testGetTagIdByName() {
        Mockito.when(tagDAO.getTagIdByName(TEST_TAG_NAME)).thenReturn(Optional.of(TEST_TAG_ID));

        int result = tagsService.getTagIdByName(TEST_TAG_NAME);

        assertEquals(TEST_TAG_ID, result);
    }

    @Test
    void testGetTagIdByNameThrowsEntityNotFoundException() {
        Mockito.when(tagDAO.getTagIdByName(TEST_TAG_NAME)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagsService.getTagIdByName(TEST_TAG_NAME));
    }

    @Test
    void testGetAll() {
        Tag tag = new Tag();
        List<Tag> tags = Collections.singletonList(tag);
        Mockito.when(tagDAO.getAll()).thenReturn(Optional.of(tags));

        TagDTOResp tagDTOResp = new TagDTOResp(TEST_TAG_ID, TEST_TAG_NAME);
        Mockito.when(tagMapper.toDTO(tag)).thenReturn(tagDTOResp);

        List<TagDTOResp> result = tagsService.getAll();

        assertEquals(Collections.singletonList(tagDTOResp), result);
    }

    @Test
    void testGetAllThrowsEntityNotFoundException() {
        Mockito.when(tagDAO.getAll()).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagsService.getAll());
    }

    @Test
    void testCreate() {
        TagDTOReq tagDTOReq = new TagDTOReq(TEST_TAG_NAME);

        Tag tag = new Tag();
        tag.setId(TEST_TAG_ID);
        tag.setName(TEST_TAG_NAME);

        Mockito.when(tagDAO.checkIfTagWithNameExists(TEST_TAG_NAME)).thenReturn(false);
        Mockito.when(tagDAO.create(Mockito.any(Tag.class))).thenReturn(Optional.of(TEST_TAG_ID));
        Mockito.when(tagMapper.toEntity(tagDTOReq)).thenReturn(tag);

        TagDTOResp tagDTOResp = new TagDTOResp(TEST_TAG_ID, TEST_TAG_NAME);
        Mockito.when(tagDAO.getById(TEST_TAG_ID)).thenReturn(Optional.of(tag));
        Mockito.when(tagMapper.toDTO(tag)).thenReturn(tagDTOResp);

        TagDTOResp result = tagsService.create(tagDTOReq);

        assertEquals(tagDTOResp, result);
        Mockito.verify(tagDAO).create(tag);
        Mockito.verify(tagDAO).getById(TEST_TAG_ID);
        Mockito.verify(tagDAO).checkIfTagWithNameExists(TEST_TAG_NAME);
    }

    @Test
    void testCreateThrowsEntityAlreadyExistsException() {
        TagDTOReq tagDTOReq = new TagDTOReq(TEST_TAG_NAME);

        Mockito.when(tagDAO.checkIfTagWithNameExists(TEST_TAG_NAME)).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> tagsService.create(tagDTOReq));
    }

    @Test
    void testCreateThrowsServiceException() {
        TagDTOReq tagDTOReq = new TagDTOReq(TEST_TAG_NAME);

        Tag tag = new Tag();
        Mockito.when(tagDAO.checkIfTagWithNameExists(TEST_TAG_NAME)).thenReturn(false);
        Mockito.when(tagDAO.create(Mockito.any(Tag.class))).thenReturn(Optional.empty());
        Mockito.when(tagMapper.toEntity(tagDTOReq)).thenReturn(tag);

        assertThrows(ServiceException.class, () -> tagsService.create(tagDTOReq));
    }

    @Test
    void testDeleteById() {
        Mockito.when(tagDAO.deleteById(TEST_TAG_ID)).thenReturn(1);

        tagsService.deleteById(TEST_TAG_ID);

        Mockito.verify(tagDAO).deleteById(TEST_TAG_ID);
    }

    @Test
    void testDeleteByIdThrowsEntityNotDeletedException() {
        Mockito.when(tagDAO.deleteById(TEST_TAG_ID)).thenReturn(0);

        assertThrows(EntityNotDeletedException.class, () -> tagsService.deleteById(TEST_TAG_ID));
    }
}
