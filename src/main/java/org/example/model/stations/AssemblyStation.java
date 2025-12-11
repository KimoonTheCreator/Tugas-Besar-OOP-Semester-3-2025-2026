package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.interfaces.Preparable;
import org.example.model.map.Position;
import org.example.model.items.Item;
import org.example.model.items.Ingredient;
import org.example.model.items.Plate;

public class AssemblyStation extends Station {

    public AssemblyStation(String name, Position position) {
        super(name, position);
    }
    public void interact(Chef chef){
        if(chef.isHoldingItem() && this.isEmpty()) {
            this.addItem(chef.dropItem());
        } else if (!chef.isHoldingItem() && !isEmpty()) {
            chef.setInventory(this.takeItem());
        } else if (chef.isHoldingItem() && !isEmpty()) {
            mergeItems(chef);
        }
    }
    private void mergeItems(Chef chef){
        Item chefItem = chef.getInventory();
        Item stationItem = this.getItem();

        if (stationItem instanceof Plate && chefItem instanceof Preparable) {
            Plate piring = (Plate) stationItem;
            Preparable bahan = (Preparable) chefItem;
            piring.addItem(bahan);
            chef.dropItem();
        }
        else if (chefItem instanceof Plate && stationItem instanceof Preparable) {
            Plate piring = (Plate) chefItem;
            Preparable bahan = (Preparable) stationItem;
            piring.addItem(bahan);
            this.takeItem();
        }
    }
}
