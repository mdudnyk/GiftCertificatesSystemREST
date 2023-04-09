package com.epam.esm.models.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Myroslav Dudnyk
 */
public class CertificateDTO {
    private String name;
    private List<String> tags;
    private String description;
    private BigDecimal price;
    private Integer duration;

    public String getName() {
        return name;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "CertificateRequestDTO{" +
                "name='" + name + '\'' +
                ", tags=" + tags +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                '}';
    }
}