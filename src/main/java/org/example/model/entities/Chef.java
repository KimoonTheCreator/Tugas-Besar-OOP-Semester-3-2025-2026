package org.example.model.entities;

import org.example.model.enums.ChefState;
import org.example.model.enums.Direction;
import org.example.model.items.Item;
import org.example.model.map.Position;
import org.example.model.map.Map;


public class Chef {
    private String id;
    private String name;
    private Position position;
    private Direction direction;
    private Item inventory;
    private ChefState state;
    private Boolean isActive;

    public Chef(String id, String name, Position position) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.direction = Direction.DOWN;
        this.state = ChefState.IDLE;
        this.inventory = null;
        this.isActive = false;
    }
    public void move(Direction direction) {
        this.direction = direction;
        //tambahkan logika update position
    }
    public void pickUpItem(Item item){
        if(!isHoldingItem()){
            this.inventory = item;
        }else{
            return;
        }
    }
    public Item dropItem(){
       //lengkapi kode ini
       // Kembalikan item di tangan,lalu set inventory jadi null
    }
    public void interactStation(Station station){
        //lengkapi kode ini
        //lakukan polymorphism dengan memanggil station.interact(this)
    }
    public Tile getFrontTile(Map gameMap){
        //lengkapi kode ini
        // hitung koordinat depan berdasarkan direction & position saat ini
    }
    public boolean isBusy(){
        return false;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public Position getPosition(){
        return position;
    }
    public void setPosition(Position position){
        this.position = position;
    }
    public Direction getDirection(){
        return direction;
    }
    public void setDirection(Direction direction){
        this.direction = direction;
    }
    public ChefState getState(){
        return state;
    }
    public void setState(ChefState state){
        this.state = state;
    }
    public Item getInventory(){
        return inventory;
    }
    public void setInventory(Item inventory){
        this.inventory = inventory;
    }
    public void setIsActive(Boolean isActive){
        this.isActive = isActive;
    }
    public Boolean getIsActive(){
        return isActive;
    }
    public boolean isHoldingItem(){
        return this.inventory != null;
    }
}