package org.example.model.order;

public class Order {
    private String name;
    private int reward = 120;
    private int penalty = -50;
    private int time;
    // private int id;

    Order(String name, int time){
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
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