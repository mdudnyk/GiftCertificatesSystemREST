package com.epam.esm.services.mappers.tag;

import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.tag.TagDTOReq;
import com.epam.esm.models.dtos.tag.TagDTOResp;

/**
 * @author Myroslav Dudnyk
 */
public interface TagMapper {
    Tag toEntity(TagDTOReq tagDTOReq);

    TagDTOResp toDTO(Tag tag);
}
