package org.example.model.items;

import org.example.model.interfaces.Preparable;
import java.util.HashSet;
import java.util.Set;

public abstract class KitchenUtensils extends Item {


    protected Set<Preparable> contents;

    public KitchenUtensils(String name) {
        super(name);
        this.contents = new HashSet<>();
    }

    public Set<Preparable> getContents() {
        return contents;
    }


    public void addItem(Preparable item) {
        this.contents.add(item);
    }


    public void emptyContents() {
        this.contents.clear();
    }
}