package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.map.Position;

public class AssemblyStation extends Station {

    public AssemblyStation(String name, Position position) {
        super(name, position);
    }
    public void interact(Chef chef){
        if(chef.isHoldingItem() && this.isEmpty()) {
            this.addItem(chef.dropItem());
        } else if (!chef.isHoldingItem() && !isEmpty()) {
            chef.setInventory(this.takeItem());
        } //gabung makanan untuk case 3
    }
}
