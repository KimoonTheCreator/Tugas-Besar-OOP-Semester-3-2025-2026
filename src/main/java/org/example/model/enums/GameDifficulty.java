package org.example.model.enums;

public enum GameDifficulty {
    EASY(300), // 5 minutes
    MEDIUM(180), // 3 minutes
    HARD(90); // 1.5 minutes

    private final int durationInSeconds;

    GameDifficulty(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }
}
