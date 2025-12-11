package org.example.model.stations;

import org.example.model.enums.ChefState;
import org.example.model.map.Position;
import org.example.model.entities.Chef;
import org.example.model.items.Ingredient;
import org.example.model.enums.IngredientState;

public class CuttingStation extends Station{
    private boolean isProcessRunning;

    public CuttingStation(String name, Position position) {
        super(name,position);
        this.isProcessRunning = false;
    }
    public void interact(Chef chef){
        if(!this.isEmpty() && this.item instanceof Ingredient){
            Ingredient bahan = (Ingredient)this.item;

            if(bahan.getState() == IngredientState.RAW && bahan.canBeChopped()){
                if(!isProcessRunning){
                    startCuttingProcess(chef, bahan);
                }
                return;
            }
        }
        if (chef.isHoldingItem() && this.isEmpty()){
            this.addItem(chef.dropItem());
        } else if (!chef.isHoldingItem() && !this.isEmpty()) {
            chef.setInventory(this.takeItem());
        }
    }
    private void startCuttingProcess(Chef chef, Ingredient bahan){
        isProcessRunning = true;

        chef.setState(ChefState.CUTTING);
        new Thread(() -> {
            try{
                while(bahan.getCuttingProgress()<100){
                    Thread.sleep(500);
                    if(chef.getState() != ChefState.CUTTING){
                        isProcessRunning = false;
                        return;
                    }
                    bahan.addCuttingProgress(17.0);

                }
            }
            catch (InterruptedException e){
                System.out.println("Cutting process interrupted");
            }finally {
                chef.setState(ChefState.IDLE);
                isProcessRunning = false;
            }
        }).start();
    }
}
