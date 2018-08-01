package com.geardao.phoneapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A Phone.
 */
@Document(collection = "phone")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "phone")
public class Phone implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("brand")
    private String brand;

    @Field("price")
    private BigDecimal price;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Phone name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public Phone brand(String brand) {
        this.brand = brand;
        return this;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Phone price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Phone phone = (Phone) o;
        if (phone.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), phone.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Phone{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", brand='" + getBrand() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
