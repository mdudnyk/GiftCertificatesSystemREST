package com.epam.esm.models.dtos;

/**
 * @author Myroslav Dudnyk
 */
public class TagDTO {
    private String name;

    public TagDTO() {
    }

    public TagDTO(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TagDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
