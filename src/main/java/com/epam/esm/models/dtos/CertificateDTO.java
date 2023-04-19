package com.epam.esm.models.dtos;

import org.springframework.lang.NonNull;

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

    public void setName(final String name) {
        this.name = name;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
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