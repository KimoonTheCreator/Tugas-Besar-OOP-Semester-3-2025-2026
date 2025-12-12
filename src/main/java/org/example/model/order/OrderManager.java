package org.example.model.order;

import org.example.model.items.Dish;
import org.example.model.items.Item;               // PENTING: Import Item
import org.example.model.interfaces.Preparable;     // PENTING: Import Interface Preparable

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.Set;                               // PENTING: Import Set

public class OrderManager {
    private Queue<Order> orders;
    private static final int MAX_CAPACITY = 5;
    private Random rand = new Random();

    // Daftar menu yang mungkin muncul
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

    /**
     * Memvalidasi apakah makanan di piring sesuai dengan pesanan terdepan.
     * @param servedItems Isi konten piring (biasanya Set<Preparable>)
     * @return true jika benar (dan pesanan dihapus), false jika salah.
     */
    public boolean validateAndRemoveOrder(Set<Preparable> servedItems) {
        if (orders.isEmpty()) {
            return false; // Tidak ada yang pesan
        }

        // 1. Intip pesanan paling depan (FIFO)
        Order currentOrder = orders.peek();
        String targetName = currentOrder.getName(); // Nama masakan yang diminta

        // 2. Cek apakah ada Item di piring yang namanya SAMA dengan pesanan
        for (Preparable p : servedItems) {
            if (p instanceof Item) {
                Item item = (Item) p;
                // Bandingkan nama (IgnoreCase agar aman, misal "Tomato" vs "tomato")
                if (item.getName().equalsIgnoreCase(targetName)) {
                    // SUKSES! Hapus pesanan dari antrian
                    orders.poll();
                    return true;
                }
            }
        }

        // Gagal (Isi piring tidak sesuai pesanan)
        return false;
    }

    public Order peekOrder() {
        return orders.peek();
    }

    public Queue<Order> getOrders() {
        return orders;
    }
}