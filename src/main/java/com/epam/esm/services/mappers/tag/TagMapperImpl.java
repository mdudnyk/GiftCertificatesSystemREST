package com.epam.esm.services.mappers.tag;

import com.epam.esm.models.Tag;
import com.epam.esm.models.dtos.tag.TagDTOReq;
import com.epam.esm.models.dtos.tag.TagDTOResp;
import org.springframework.stereotype.Component;

/**
 * @author Myroslav Dudnyk
 */
@Component
public class TagMapperImpl implements TagMapper {
    @Override
    public Tag toEntity(final TagDTOReq tagDTOReq) {
        return new Tag(tagDTOReq.name());
    }

    @Override
    public TagDTOResp toDTO(final Tag tag) {
        return new TagDTOResp(tag.getId(), tag.getName());
    }
}
