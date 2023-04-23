package com.epam.esm.services;

import com.epam.esm.models.dtos.tag.TagDTOReq;
import com.epam.esm.models.dtos.tag.TagDTOResp;

import java.util.List;
import java.util.Optional;

/**
 * @author Myroslav Dudnyk
 */
public interface TagsService {
    List<String> getAllNamesByCertificateId(int certificateId);

    Optional<Integer> getTagIdByName(String name);

    TagDTOResp getById(int id);

    TagDTOResp create(TagDTOReq tagDAOReq);
}
