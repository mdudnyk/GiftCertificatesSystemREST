package com.epam.esm.models;

/**
 * @author Myroslav Dudnyk
 */
public class TagDTO {
    private String name;

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
