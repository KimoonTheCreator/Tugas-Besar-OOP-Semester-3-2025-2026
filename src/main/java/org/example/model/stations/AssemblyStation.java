package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Ingredient;
import org.example.model.items.Item;
import org.example.model.items.Pizza;
import org.example.model.map.Position;
import org.example.model.recipe.Recipe;
import org.example.model.recipe.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class AssemblyStation extends Station {

    // Stack untuk menumpuk bahan (Apapun statenya)
    private List<Ingredient> ingredientStack;

    // Slot untuk Pizza yang sudah jadi (atau Pizza yang ditaruh kembali)
    private Pizza resultPizza;

    public AssemblyStation(String name, Position position) {
        super(name, position);
        this.ingredientStack = new ArrayList<>();
        this.resultPizza = null;
    }

    // KEY V: INTERACT (Stack / Merge / Ambil / Taruh)
    @Override
    public void interact(Chef chef) {

        // ============================================================
        // 1. CHEF BAWA ITEM (LOGIKA MENARUH / STACKING)
        // ============================================================
        if (chef.isHoldingItem()) {
            Item itemHand = chef.getInventory();

            // KASUS A: CHEF BAWA INGREDIENT (BAHAN)
            // Syarat: Meja tidak boleh ada Pizza jadi.
            if (itemHand instanceof Ingredient) {
                if (resultPizza == null) {
                    // Masukkan ke stack (BEBAS RAW/CHOPPED)
                    ingredientStack.add((Ingredient) chef.dropItem());
                    System.out.println("Menumpuk bahan. Total stack: " + ingredientStack.size());
                } else {
                    System.out.println("Meja penuh ada Pizza, tidak bisa tumpuk bahan!");
                }
                return;
            }

            // KASUS B: CHEF BAWA PIZZA (TARUH BALIK)
            // Syarat: Meja harus benar-benar kosong (gak ada stack, gak ada pizza lain)
            if (itemHand instanceof Pizza) {
                if (isEmpty()) {
                    this.resultPizza = (Pizza) chef.dropItem();
                    System.out.println("Menaruh Pizza kembali ke meja.");
                } else {
                    System.out.println("Meja tidak kosong!");
                }
                return;
            }

            return; // Item lain (piring dll) tidak dihandle disini
        }

        // ============================================================
        // 2. CHEF TANGAN KOSONG (LOGIKA AMBIL / MERGE)
        // ============================================================
        if (!chef.isHoldingItem()) {

            // KASUS C: ADA PIZZA JADI -> AMBIL
            if (resultPizza != null) {
                chef.setInventory(resultPizza);
                resultPizza = null;
                System.out.println("Chef mengambil Pizza.");
                return;
            }

            // KASUS D: ADA TUMPUKAN BAHAN -> COBA MERGE
            if (!ingredientStack.isEmpty()) {

                // Cek Recipe Manager
                Recipe foundRecipe = RecipeManager.getInstance().findMatchingRecipe(ingredientStack);

                if (foundRecipe != null) {
                    // --- SUKSES MERGING ---
                    // Buat Pizza baru sesuai nama resep
                    this.resultPizza = new Pizza(foundRecipe.getName());
                    // Stack dibersihkan karena sudah jadi Pizza
                    this.ingredientStack.clear();

                    System.out.println("MERGING SUKSES! Jadi " + resultPizza.getName());
                } else {
                    // --- GAGAL MERGING ---
                    // Jika tidak sesuai resep, Chef mengambil bahan paling atas (Undo)
                    // Ini penting agar pemain bisa memperbaiki kesalahan tumpuk
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
        // Prioritas 1: Tampilkan Pizza (Baik hasil rakitan atau ditaruh balik)
        if (resultPizza != null) return resultPizza;

        // Prioritas 2: Tampilkan bahan paling atas tumpukan
        if (!ingredientStack.isEmpty()) {
            return ingredientStack.get(ingredientStack.size() - 1);
        }

        return null; // Meja kosong
    }

    @Override
    public boolean isEmpty() {
        return resultPizza == null && ingredientStack.isEmpty();
    }

    // Method removeItem default (Hanya dipakai jika logika interact default dipanggil, disini kita custom)
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