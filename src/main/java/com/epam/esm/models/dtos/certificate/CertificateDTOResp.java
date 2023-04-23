package com.epam.esm.models.dtos.certificate;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
public record CertificateDTOResp(
        Integer id,
        String name,
        List<String> tags,
        String description,
        BigDecimal price,
        Integer duration
) {
}
