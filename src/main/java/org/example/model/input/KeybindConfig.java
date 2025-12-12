package org.example.model.input;

import org.example.model.enums.Key;
import org.example.model.enums.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Kelas untuk konfigurasi keybind (pemetaan tombol ke command)
 */
public class KeybindConfig {
    private Map<Key, Command> keyBindings;

    // Set untuk command pergerakan
    private static final Set<Command> MOVEMENT_COMMANDS = Set.of(
            Command.MOVE_UP, Command.MOVE_DOWN, Command.MOVE_LEFT, Command.MOVE_RIGHT);

    public KeybindConfig() {
        this.keyBindings = new HashMap<>();
        initDefaultBindings();
    }

    // Inisialisasi binding default
    private void initDefaultBindings() {
        keyBindings.put(Key.W, Command.MOVE_UP);
        keyBindings.put(Key.S, Command.MOVE_DOWN);
        keyBindings.put(Key.A, Command.MOVE_LEFT);
        keyBindings.put(Key.D, Command.MOVE_RIGHT);
        keyBindings.put(Key.V, Command.INTERACT);
        keyBindings.put(Key.F, Command.PICKUP_DROP);
        keyBindings.put(Key.P, Command.PAUSE);
        keyBindings.put(Key.TAB, Command.SWITCH_CHEF);
    }

    // Dapatkan command dari key
    public Command getCommand(Key key) {
        return keyBindings.getOrDefault(key, Command.NONE);
    }

    // Cek apakah command adalah movement
    public boolean isMovementCommand(Command command) {
        return MOVEMENT_COMMANDS.contains(command);
    }

    // Cek apakah key adalah movement key
    public boolean isMovementKey(Key key) {
        return isMovementCommand(getCommand(key));
    }

    // Remap tombol
    public void remapKey(Key key, Command command) {
        keyBindings.put(key, command);
    }

    // Reset ke default
    public void resetToDefault() {
        keyBindings.clear();
        initDefaultBindings();
    }
}
