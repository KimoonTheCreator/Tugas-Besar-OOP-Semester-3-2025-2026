package org.example.model.items;

import java.util.ArrayList;
import java.util.List;

public class Dish extends Item {
    private List<Ingredient> components;

    public Dish (String name){
        super(name);
        this.components = new ArrayList<>();
    }
    public void addComponent (Ingredient ingredient){
        //Tambahkan bahan ke dalam list
    }
    public List<Ingredient> getComponents(){
        return this.components;
    }
}
