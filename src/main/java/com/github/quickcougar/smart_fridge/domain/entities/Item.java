package com.github.quickcougar.smart_fridge.domain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Item {

    @JsonProperty
    @EqualsAndHashCode.Include()
    private Integer id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String type;

    public Item() {
    }

    public Item(Integer id,
                String name,
                String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Item(String name, String type) {
        this(null, name, type);
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
