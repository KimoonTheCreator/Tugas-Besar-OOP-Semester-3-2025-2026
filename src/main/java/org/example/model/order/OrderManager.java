package org.example.model.order;

import org.example.model.items.Dish;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

public class OrderManager {
    private Queue<Order> orders;
    private static final int MAX_CAPACITY = 5; //ini ga harus 5, tpi boleh diset dari awal aja
    private Random rand = new Random();
    private String[] Dishes = {"Margherita", "Sausage", "Chicken"};
    

    public OrderManager() {
        orders = new ArrayDeque<>();
    }

    public void generateOrder() {

        if (orders.size() >= MAX_CAPACITY) {
            return;
        }

        String randomDish = Dishes[rand.nextInt(Dishes.length)];

        Dish targetDish = new Dish(randomDish);

        int randomTime = 30 + rand.nextInt(31); // 30–60 detik
        Order randomOrder = new Order(targetDish, randomTime);

        orders.offer(randomOrder);
    }


    public void completeOrder() {
        if (orders.isEmpty()) {
            return;
        }

        orders.poll();
    }
    public Order peekOrder() {
        return orders.peek();
    }

    public Queue<Order> getOrders() {
        return orders;
    }
}
