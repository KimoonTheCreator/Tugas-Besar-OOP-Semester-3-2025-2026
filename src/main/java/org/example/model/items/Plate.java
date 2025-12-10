package org.example.model.items;

import java.util.ArrayList;
import java.util.List;

public class Plate extends Item {
    boolean clean;
    List<Ingredient> cookedIngredients;

    public Plate(String name){
        super(name);
        this.clean = true;
        cookedIngredients = new ArrayList<>();
    }

    public void addIngredients(Ingredient ingredient){
        cookedIngredients.add(ingredient);
    }

    public Dish createDish(String name){
        Dish d = new Dish(name);
        d.setComponents(cookedIngredients);
        this.clean = false;
        cookedIngredients = new ArrayList<>();
        return d;        
    }

    public void wash(){
        clean = true;
    }

    public boolean isClean() {
        return clean;
    }  
}
