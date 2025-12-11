package org.example.model.input;

import org.example.model.enums.Key;
import org.example.model.enums.Command;
import org.example.model.map.Direction;

/**
 * Kelas InputHandler untuk menerjemahkan input keyboard menjadi command
 */
public class InputHandler {
    private KeybindConfig keybindConfig;

    public InputHandler() {
        this.keybindConfig = new KeybindConfig();
    }

    public InputHandler(KeybindConfig keybindConfig) {
        this.keybindConfig = keybindConfig;
    }

    // Terjemahkan key menjadi command
    public Command handleInput(Key key) {
        if (key == null)
            return Command.NONE;
        return keybindConfig.getCommand(key);
    }

    // Konversi command movement menjadi Direction
    public Direction getDirectionFromCommand(Command command) {
        if (command == null)
            return null;

        switch (command) {
            case MOVE_UP:
                return Direction.UP;
            case MOVE_DOWN:
                return Direction.DOWN;
            case MOVE_LEFT:
                return Direction.LEFT;
            case MOVE_RIGHT:
                return Direction.RIGHT;
            default:
                return null;
        }
    }

    // Konversi key langsung ke Direction
    public Direction getDirectionFromKey(Key key) {
        Command command = handleInput(key);
        return getDirectionFromCommand(command);
    }

    // Cek apakah command adalah movement
    public boolean isMovementCommand(Command command) {
        return keybindConfig.isMovementCommand(command);
    }

    // Getter dan Setter
    public KeybindConfig getKeybindConfig() {
        return keybindConfig;
    }

    public void setKeybindConfig(KeybindConfig keybindConfig) {
        this.keybindConfig = keybindConfig;
    }
}
