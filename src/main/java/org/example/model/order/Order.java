package org.example.model.order;
import org.example.model.items.Dish;

public class Order {
    private Dish dish;
    private int reward = 120;
    private int penalty = -50;
    private int time;
    // private int id;

    public Order(Dish dish, int time){
        this.dish = dish;
        this.time = time;
    }

    public String getName() {
        return dish.getName();
    }

    public int getPenalty() {
        return penalty;
    }

    public int getReward() {
        return reward;
    }

    public int getTime() {
        return time;
    }

    // public int getId() {
    //     return id;
    // }

    // public void setId(int id) {
    //     this.id = id;
    // }
}