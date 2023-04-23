package com.epam.esm.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Myroslav Dudnyk
 */
public class Tag implements Serializable {
    private int id;

    private String name;

    public Tag() {
    }

    public Tag(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Tag(final String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Tag tag = (Tag) o;
        return id == tag.id && name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
