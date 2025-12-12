package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.interfaces.Preparable;
import org.example.model.map.Position;
import org.example.model.items.Item;
import org.example.model.items.Ingredient;
import org.example.model.items.Item;
import org.example.model.items.Pizza;
import org.example.model.map.Position;
import org.example.model.recipe.Recipe;
import org.example.model.recipe.RecipeManager;

import java.util.ArrayList;
import java.util.List;
import org.example.model.items.Plate;
import org.example.model.items.Dish;
import org.example.model.items.KitchenUtensils;
import org.example.model.recipe.Recipe;
import org.example.model.recipe.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class AssemblyStation extends Station {

    // Stack untuk menumpuk bahan (Apapun statenya)
    private List<Ingredient> ingredientStack;

    // Slot untuk Pizza yang sudah jadi (atau Pizza yang ditaruh kembali)
    private Pizza resultPizza;

    // Slot untuk Piring yang diletakkan di AssemblyStation
    private Plate storedPlate;

    public AssemblyStation(String name, Position position) {
        super(name, position);
        this.ingredientStack = new ArrayList<>();
        this.resultPizza = null;
        this.storedPlate = null;
    }

    // KEY V: INTERACT (Stack / Merge / Ambil / Taruh)
    @Override
    public void interact(Chef chef) {

        // ============================================================
        // 1. CHEF BAWA ITEM
        // ============================================================
        if (chef.isHoldingItem()) {
            Item itemHand = chef.getInventory();

            // KASUS A: CHEF BAWA BAHAN (INGREDIENT)
            if (itemHand instanceof Ingredient) {
                if (storedPlate != null) {
                    // Kalau ada piring, masukin bahan ke piring? (Opsional, tapi biasanya plating
                    // Pizza lgsg jadi)
                    // Kita assume piring di assembly cuma buat plating pizza jadi.
                    System.out.println("Ada piring.. logic menumpuk bahan ke piring belum disupport spesifik.");
                } else if (resultPizza == null) {
                    ingredientStack.add((Ingredient) chef.dropItem());
                    System.out.println("Menumpuk bahan. Total stack: " + ingredientStack.size());
                } else {
                    System.out.println("Meja penuh ada Pizza, tidak bisa tumpuk bahan!");
                }
                return;
            }

            // KASUS B: CHEF BAWA PIRING (PLATING / TARUH PIRING)
            if (itemHand instanceof Plate) {
                Plate piringTangan = (Plate) itemHand;

                // Scenario B1: Ada Pizza Jadi di Meja -> Pindahkan ke Piring Tangan
                if (resultPizza != null) {
                    if (piringTangan.getContents().isEmpty()) {
                        piringTangan.addContent(resultPizza);
                        resultPizza = null;
                        System.out.println("Plating SUKSES! Pizza masuk ke piring.");
                    }
                    return;
                }

                // Scenario B2: Ada Tumpukan Bahan -> Merge jadi Pizza -> Masuk Piring Tangan
                if (!ingredientStack.isEmpty()) {
                    Recipe foundRecipe = RecipeManager.getInstance().findMatchingRecipe(ingredientStack);
                    if (foundRecipe != null) {
                        Pizza instantPizza = new Pizza(foundRecipe.getName());
                        piringTangan.addContent(instantPizza);
                        ingredientStack.clear();
                        System.out.println("Plating + Merging SUKSES! Instantly served.");
                    }
                    return;
                }

                // Scenario B3: Meja Kosong -> Taruh Piring
                if (isEmpty() && storedPlate == null) {
                    this.storedPlate = (Plate) chef.dropItem();
                    System.out.println("Menaruh Piring di meja.");
                    return;
                }

                return;
            }

            // KASUS C: CHEF BAWA PIZZA (TARUH BALIK / PLATING KE STORED PLATE)
            if (itemHand instanceof Pizza) {
                Pizza pizzaTangan = (Pizza) itemHand;

                // C1: Ada Piring di meja (StoredPlate) -> Masukkan Pizza ke Piring Meja
                if (storedPlate != null) {
                    if (storedPlate.getContents().isEmpty()) {
                        // FIX: Cast to Preparable or specific type (Pizza is Preparable)
                        storedPlate.addContent((Preparable) chef.dropItem());
                        System.out.println("Meletakkan Pizza ke atas Piring di Meja.");
                    }
                    return;
                }

                // C2: Meja Kosong -> Taruh Pizza
                if (isEmpty()) {
                    this.resultPizza = (Pizza) chef.dropItem();
                    System.out.println("Menaruh Pizza kembali ke meja.");
                } else {
                    System.out.println("Meja tidak kosong!");
                }
                return;
            }
            return;
        }

        // ============================================================
        // 2. CHEF TANGAN KOSONG (AMBIL)
        // ============================================================
        if (!chef.isHoldingItem()) {

            // Prioritas Ambil:
            // 1. Piring (StoredPlate) -> dengan atau tanpa isi
            if (storedPlate != null) {
                chef.setInventory(storedPlate);
                storedPlate = null;
                System.out.println("Mengambil Piring dari meja.");
                return;
            }

            // 2. Pizza Jadi
            if (resultPizza != null) {
                chef.setInventory(resultPizza);
                resultPizza = null;
                System.out.println("Chef mengambil Pizza.");
                return;
            }

            // 3. Merge Stack / Undo Stack
            if (!ingredientStack.isEmpty()) {
                Recipe foundRecipe = RecipeManager.getInstance().findMatchingRecipe(ingredientStack);

                if (foundRecipe != null) {
                    // Berhasil Merge -> Jadi Pizza -> Ambil Pizza
                    Pizza successPizza = new Pizza(foundRecipe.getName());
                    chef.setInventory(successPizza);
                    ingredientStack.clear();
                    System.out.println("MERGING & PICKUP SUKSES! " + successPizza.getName());
                } else {
                    // Gagal Merge -> Undo (Ambil bahan paling atas)
                    Ingredient top = ingredientStack.remove(ingredientStack.size() - 1);
                    chef.setInventory(top);
                    System.out.println("Resep belum cocok. Mengambil " + top.getName());
                }
            }
        }
    }

    // ============================================================
    // VISUAL UPDATE (Agar AssetManager tahu gambar apa yg dimunculkan)
    // ============================================================
    @Override
    public Item getItem() {
        if (storedPlate != null)
            return storedPlate; // SHOW PLATE

        // Prioritas 1: Tampilkan Pizza (Baik hasil rakitan atau ditaruh balik)
        if (resultPizza != null)
            return resultPizza;

        // Prioritas 2: Tampilkan bahan paling atas tumpukan
        if (!ingredientStack.isEmpty()) {
            return ingredientStack.get(ingredientStack.size() - 1);
        }

        return null; // Meja kosong
    }

    @Override
    public boolean isEmpty() {
        return resultPizza == null && ingredientStack.isEmpty() && storedPlate == null;
    }

    // Method removeItem default (Hanya dipakai jika logika interact default
    // dipanggil, disini kita custom)
    @Override
    public Item removeItem() {
        if (resultPizza != null) {
            Item temp = resultPizza;
            resultPizza = null;
            return temp;
        }
        if (!ingredientStack.isEmpty()) {
            return ingredientStack.remove(ingredientStack.size() - 1);
        }
        return null;
    }
}