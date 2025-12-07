package org.example.model.items;

import org.example.model.interfaces.Prepareable;
import org.example.model.enums.IngredientState;

public class Ingredients extends Item implements Prepareable {
    private IngredientState state;

    public Ingredients(String name){
        super(name);
        this.state = IngredientState.RAW;
    }

    public IngredientState getState() {
        return state;
    }

    public void setState(IngredientState state) {
        this.state = state;
    }
    //Implement fungsi pada prepareable
    public boolean canBeChopped(){
        return this.state == IngredientState.RAW;
    }
    public boolean canBeCooked(){
        return this.state == IngredientState.RAW || this.state == IngredientState.CHOPPED;
    }
    public boolean canBePlacedOnPlate(){
        return true;
    }
    public void chop(){
        if (canBeChopped()){
            this.state = IngredientState.CHOPPED;
        }else{
            return;
        }
    }
    public void cook(){
        if (canBeCooked()){
            this.state = IngredientState.COOKED;
        }else{
            return;
        }
    }
}