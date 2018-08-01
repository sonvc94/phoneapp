package com.geardao.phoneapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Brand entity.
 */
public class BrandDTO implements Serializable {

    private String id;

    private String name;

    private String country;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BrandDTO brandDTO = (BrandDTO) o;
        if (brandDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), brandDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BrandDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", country='" + getCountry() + "'" +
            "}";
    }
}
