# 🍕 Wallahi I'm Cooked - Cooking Game

<div align="center">

**Tugas Besar Object Oriented Programming**  
*Sistem dan Teknologi Informasi - Semester 3 (2025/2026)*

</div>

---

## 📖 Deskripsi

**Wallahi I'm Cooked** adalah game simulasi memasak pizza yang terinspirasi dari game Overcooked. Pemain mengontrol chef untuk menyiapkan, memasak, dan menyajikan pizza sesuai dengan pesanan pelanggan dalam batas waktu tertentu.

### ✨ Fitur Utama

- 🎮 **Dual Chef System** - Kontrol 2 chef dan switch di antara mereka
- 🍕 **Sistem Memasak Realistis** - Potong bahan, assembly pizza, panggang di oven
- ⏱️ **Order Timer** - Selesaikan pesanan sebelum waktu habis
- 🎯 **Multiple Difficulty** - Easy, Medium, Hard dengan durasi berbeda
- 🔥 **Burning System** - Pizza bisa gosong jika terlalu lama di oven!
- 🧹 **Plate Washing** - Cuci piring kotor sebelum digunakan kembali

---

## 🎯 Gameplay

1. **Ambil Bahan** - Ambil bahan dari storage (Tomat, Keju, Adonan, dll.)
2. **Potong Bahan** - Potong bahan di Cutting Station
3. **Assembly Pizza** - Gabungkan bahan di Assembly Station
4. **Panggang** - Masukkan pizza mentah ke Oven
5. **Plating** - Taruh pizza matang di piring bersih
6. **Sajikan** - Antarkan ke Serving Counter untuk mendapat poin!

---

## ⏱️ Kondisi Akhir Stage

Stage dapat berakhir dengan 2 kondisi:

### 1. ⌛ Time's Up! (Waktu Habis)
- Timer countdown mencapai 0
- Game berhenti menerima order baru
- Total score dihitung dan dievaluasi
- **PASS** jika score ≥ minimum score
- **FAIL** jika score < minimum score

### 2. ❌ Too Many Failed Orders
- Player gagal menyelesaikan order berturut-turut
- Stage langsung berakhir dengan status **FAIL**
- Player harus retry stage

### 📊 Difficulty Settings

| Difficulty | Durasi | Min. Score | Max Fails |
|------------|--------|------------|-----------|
| **Easy** | 5 menit | 150 pts | 5 order |
| **Medium** | 3 menit | 200 pts | 4 order |
| **Hard** | 1.5 menit | 250 pts | 3 order |

---

## 🚀 Cara Menjalankan

1. **Clone Repository**
   ```bash
   git clone https://github.com/KimoonTheCreator/Tugas-Besar-OOP-Semester-3-2025-2026.git
   cd Tugas-Besar-OOP-Semester-3-2025-2026
   ```

2. **Build Project**
   ```bash
   # Windows
   .\gradlew build
   
   # Linux/macOS
   ./gradlew build
   ```

3. **Jalankan Game**
   ```bash
   # Windows
   .\gradlew run
   
   # Linux/macOS
   ./gradlew run
   ```

### Alternatif: Clean Build
```bash
.\gradlew clean build run
```

---

## 🎮 Kontrol Permainan

| Tombol | Fungsi |
|--------|--------|
| `W` `A` `S` `D` | Gerakan Chef (Atas, Kiri, Bawah, Kanan) |
| `F` | Pickup / Drop Item |
| `V` | Interact dengan Station |
| `C` | Action (Potong, Cuci, dll.) |
| `SPACE` | Dash (dengan cooldown) |
| `TAB` | Switch Chef |
| `ESC` | Pause Menu |

---

## 🏗️ Struktur Proyek

```
src/main/java/org/example/
├── 📂 controller/
│   ├── GameController.java       # Main game logic & loop
│   ├── MainMenuController.java   # Main menu UI
│   ├── PauseMenuController.java  # Pause menu 
│   └── StageOverController.java  # Game over screen
│
├── 📂 model/
│   ├── 📂 entities/
│   │   ├── Chef.java             # Player character
│   │   └── GameObject.java       # Base class
│   │
│   ├── 📂 enums/
│   │   ├── ChefState.java        # Chef states
│   │   ├── IngredientState.java  # Item states (RAW/COOKED/BURNED)
│   │   ├── GameDifficulty.java   # Difficulty settings
│   │   └── ...
│   │
│   ├── 📂 items/
│   │   ├── Item.java             # Abstract base item
│   │   ├── Ingredient.java       # Raw ingredients
│   │   ├── Pizza.java            # Completed pizza
│   │   ├── Plate.java            # Serving plate
│   │   └── ...
│   │
│   ├── 📂 stations/
│   │   ├── Station.java          # Abstract station
│   │   ├── CuttingStation.java   # Chop ingredients
│   │   ├── CookingStation.java   # Abstract cooking
│   │   ├── AssemblyStation.java  # Merge ingredients
│   │   ├── WashingStation.java   # Wash plates
│   │   └── ...
│   │
│   ├── 📂 map/
│   │   ├── GameMap.java          # Map layout
│   │   ├── Tile.java             # Map tiles
│   │   └── Position.java         # Coordinates
│   │
│   ├── 📂 recipe/
│   │   ├── Recipe.java           # Recipe definition
│   │   └── RecipeManager.java    # Recipe management (Singleton)
│   │
│   └── 📂 order/
│       ├── Order.java            # Customer order
│       └── OrderManager.java     # Order queue
│
├── 📂 view/
│   └── AssetManager.java         # Asset loading
│
└── Main.java                     # Entry point
```

---

## 📚 Konsep OOP yang Diimplementasikan

| No | Konsep | Implementasi |
|----|--------|--------------|
| 1 | **Inheritance** | `Dish extends Item`, `Station extends GameObject`, `Oven extends CookingStation` |
| 2 | **Abstract Class** | `Item`, `Station`, `CookingStation`, `GameObject` |
| 3 | **Interface** | `Preparable`, `GameView` |
| 4 | **Polymorphism** | Override `interact()`, `shouldAcceptItem()`, `toString()` |
| 5 | **Generics** | `List<Recipe>`, `Map<Key, Command>`, `Set<Preparable>` |
| 6 | **Exceptions** | Try-catch di controller dan asset loading |
| 7 | **Collections** | `ArrayList`, `HashMap`, `HashSet`, `Stack`, `Queue` |
| 8 | **Concurrency** | `AnimationTimer` untuk game loop |

---

## 🎨 Design Patterns

| Pattern | Implementasi |
|---------|--------------|
| **Singleton** | `RecipeManager.getInstance()` |
| **MVC** | Model-View-Controller architecture |
| **State** | `ChefState`, `IngredientState`, `GameState` enums |
| **Template Method** | `Station.interact()`, `CookingStation.shouldAcceptItem()` |
| **Factory** | `GameMap.initializeStationLogic()` |
| **Game Loop** | `GameController.startGameLoop()` |
| **Strategy** | Interface `Preparable` |
| **Command** | `KeybindConfig` dengan enum `Command` |

---

## ⚖️ SOLID Principles

| Principle | Implementasi |
|-----------|--------------|
| **SRP** | Setiap class memiliki satu tanggung jawab |
| **OCP** | Station extensible tanpa modifikasi base class |
| **LSP** | Item subclasses dapat menggantikan parent |
| **ISP** | Interface `Preparable` hanya untuk item yang bisa diproses |
| **DIP** | Dependency pada abstraksi (Item, Station, Preparable) |

---

## 🍕 Resep Pizza

| Pizza | Bahan |
|-------|-------|
| **Margherita** | Adonan (Chopped) + Tomat (Chopped) + Keju (Chopped) |
| **Sausage** | Adonan (Chopped) + Tomat (Chopped) + Keju (Chopped) + Sosis (Chopped) |
| **Chicken** | Adonan (Chopped) + Tomat (Chopped) + Keju (Chopped) + Ayam (Chopped) |

---

## 👥 Tim Pengembang

Tugas Besar OOP - STI ITB 2025/2026
18224032 Claudia Melati Krid
18224034 Wa Ode Amerta Lambelu. J
18224076 Bram Sebastian Pangaribuan
18224084 Muhammad Ghiffary Alfathan


---

<div align="center">

**Made with ❤️ and ☕ by STI Students**

</div>
