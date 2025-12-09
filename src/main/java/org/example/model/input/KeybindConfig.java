package org.example.model.input;

import org.example.model.enums.Key;
import org.example.model.enums.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Kelas untuk konfigurasi keybind (pemetaan tombol ke command)
 * Mendukung keybind berbeda untuk setiap action
 */
public class KeybindConfig {
    private Map<Key, Command> keyBindings;

    // Set untuk command pergerakan
    private static final Set<Command> MOVEMENT_COMMANDS = Set.of(
            Command.MOVE_UP, Command.MOVE_DOWN, Command.MOVE_LEFT, Command.MOVE_RIGHT);

    // Set untuk action commands
    private static final Set<Command> ACTION_COMMANDS = Set.of(
            Command.PICKUP, Command.DROP, Command.CUT, Command.COOK, Command.WASH, Command.INTERACT);

    public KeybindConfig() {
        this.keyBindings = new HashMap<>();
        initDefaultBindings();
    }

    /**
     * Inisialisasi binding default
     * 
     * Control Scheme:
     * - Movement: W A S D
     * - Pickup Item: Q
     * - Drop Item: F
     * - Cut (Cutting Station): C
     * - Wash (Washing Station): X
     * - Cook/Use (Cooking Station): R
     * - General Interact: E
     * - Switch Chef: TAB
     * - Pause/Menu: P
     * - Legacy Pickup/Drop: V
     */
    private void initDefaultBindings() {
        // Movement
        keyBindings.put(Key.W, Command.MOVE_UP);
        keyBindings.put(Key.S, Command.MOVE_DOWN);
        keyBindings.put(Key.A, Command.MOVE_LEFT);
        keyBindings.put(Key.D, Command.MOVE_RIGHT);

        // Item Handling
        keyBindings.put(Key.Q, Command.PICKUP);
        keyBindings.put(Key.F, Command.DROP);
        keyBindings.put(Key.V, Command.PICKUP_DROP); // Legacy support

        // Station-Specific Actions
        keyBindings.put(Key.C, Command.CUT); // Cutting Station
        keyBindings.put(Key.X, Command.WASH); // Washing Station
        keyBindings.put(Key.R, Command.COOK); // Cooking Station

        // General
        keyBindings.put(Key.E, Command.INTERACT);

        // System
        keyBindings.put(Key.TAB, Command.SWITCH_CHEF);
        keyBindings.put(Key.P, Command.PAUSE);
    }

    /**
     * Dapatkan command dari key
     */
    public Command getCommand(Key key) {
        return keyBindings.getOrDefault(key, Command.NONE);
    }

    /**
     * Cek apakah command adalah movement
     */
    public boolean isMovementCommand(Command command) {
        return MOVEMENT_COMMANDS.contains(command);
    }

    /**
     * Cek apakah command adalah action
     */
    public boolean isActionCommand(Command command) {
        return ACTION_COMMANDS.contains(command);
    }

    /**
     * Cek apakah key adalah movement key
     */
    public boolean isMovementKey(Key key) {
        return isMovementCommand(getCommand(key));
    }

    /**
     * Remap tombol ke command tertentu
     */
    public void remapKey(Key key, Command command) {
        keyBindings.put(key, command);
    }

    /**
     * Get key for specific command (untuk display di UI)
     */
    public Key getKeyForCommand(Command command) {
        for (Map.Entry<Key, Command> entry : keyBindings.entrySet()) {
            if (entry.getValue() == command) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Get display string untuk keybind
     */
    public String getKeybindDisplay(Command command) {
        Key key = getKeyForCommand(command);
        return key != null ? key.toString() : "?";
    }

    /**
     * Reset ke default
     */
    public void resetToDefault() {
        keyBindings.clear();
        initDefaultBindings();
    }

    /**
     * Get all keybindings (untuk display)
     */
    public Map<Key, Command> getAllBindings() {
        return new HashMap<>(keyBindings);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Keybind Configuration:\n");
        sb.append("=== Movement ===\n");
        sb.append("  Move Up: ").append(getKeybindDisplay(Command.MOVE_UP)).append("\n");
        sb.append("  Move Down: ").append(getKeybindDisplay(Command.MOVE_DOWN)).append("\n");
        sb.append("  Move Left: ").append(getKeybindDisplay(Command.MOVE_LEFT)).append("\n");
        sb.append("  Move Right: ").append(getKeybindDisplay(Command.MOVE_RIGHT)).append("\n");
        sb.append("=== Actions ===\n");
        sb.append("  Pickup: ").append(getKeybindDisplay(Command.PICKUP)).append("\n");
        sb.append("  Drop: ").append(getKeybindDisplay(Command.DROP)).append("\n");
        sb.append("  Cut: ").append(getKeybindDisplay(Command.CUT)).append("\n");
        sb.append("  Cook: ").append(getKeybindDisplay(Command.COOK)).append("\n");
        sb.append("  Wash: ").append(getKeybindDisplay(Command.WASH)).append("\n");
        sb.append("  Interact: ").append(getKeybindDisplay(Command.INTERACT)).append("\n");
        sb.append("=== System ===\n");
        sb.append("  Switch Chef: ").append(getKeybindDisplay(Command.SWITCH_CHEF)).append("\n");
        return sb.toString();
    }
}
