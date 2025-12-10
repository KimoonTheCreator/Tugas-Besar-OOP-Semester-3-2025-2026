package org.example.model.items;

import org.example.model.interfaces.Preparable;
import java.util.HashSet;
import java.util.Set;


public abstract class KitchenUtensils extends Item {

    protected Set<Preparable> contents;

    public KitchenUtensils(String name){
        super(name);
        this.contents = new HashSet<>();
    }
    public boolean isPortable(){
        return true;
    }

    public Set<Preparable> getContents() {
        return contents;
    }
    public void addItem(Preparable item){
        this.contents.add(item);
    }
    public void  removeItem(){
        this.contents.clear();
    }
    public int capacity(){
        //return kapasitas maksimal suatu bahan
        return 0;
    }
    public boolean canAccept(Preparable ingredient){
        //cek apakah bahan cocok dengan alat ini?
        return false;
    }
    public void addIngredient(Preparable ingredient){
        //masukkan bahan ke alat ini
        
    }
    public void startCooking(){
        //ubah status bahan di dalamnya dari COOKING menjadi COOKED
    }
}
