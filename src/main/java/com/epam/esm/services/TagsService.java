package com.epam.esm.services;

import com.epam.esm.models.dtos.tag.TagDTOReq;
import com.epam.esm.models.dtos.tag.TagDTOResp;

import java.util.List;


/**
 * @author Myroslav Dudnyk
 */
public interface TagsService {
    TagDTOResp getById(int id);

    List<String> getAllNamesByCertificateId(int certificateId);

    int getTagIdByName(String tagName);

    List<TagDTOResp> getAll();

    TagDTOResp create(TagDTOReq tagDAOReq);

    void deleteById(int id);
}
