package com.github.quickcougar.smart_fridge.domain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Fridge {

    @JsonProperty
    @EqualsAndHashCode.Include()
    private Integer id;

    @JsonProperty
    private String name;

    @JsonProperty
    private List<Item> items;

    public Fridge() {
    }

    public Fridge(Integer id,
                  String name,
                  List<Item> items
    ) {
        this.id = id;
        this.name = name;
        this.items = items;
    }

    public Fridge(String name, List<Item> items) {
        this(null, name, items);
    }

    public Fridge(String name) {
        this(null, name, null);
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

    public List<Item> getItems() {
        return this.items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void merge(Fridge newFridge) {
        if (newFridge.getId() != null) {
            this.id = newFridge.getId();
        }
        if (newFridge.getName() != null) {
            this.name = newFridge.getName();
        }
        if (newFridge.getItems() != null) {
            this.items = Stream.of(this.items, newFridge.getItems())
                    .flatMap(x -> x.stream())
                    .collect(Collectors.toList());
        }
    }
}
