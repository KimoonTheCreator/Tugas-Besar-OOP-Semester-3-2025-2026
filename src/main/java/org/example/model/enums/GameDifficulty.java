package org.example.model.enums;

public enum GameDifficulty {
    EASY(300, 150, 5), // 5 minutes, min score 150, max 5 consecutive fails
    MEDIUM(180, 200, 4), // 3 minutes, min score 200, max 4 consecutive fails
    HARD(90, 250, 3); // 1.5 minutes, min score 250, max 3 consecutive fails

    private final int durationInSeconds;
    private final int minimumScoreToPass;
    private final int maxConsecutiveFails;

    GameDifficulty(int durationInSeconds, int minimumScoreToPass, int maxConsecutiveFails) {
        this.durationInSeconds = durationInSeconds;
        this.minimumScoreToPass = minimumScoreToPass;
        this.maxConsecutiveFails = maxConsecutiveFails;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public int getMinimumScoreToPass() {
        return minimumScoreToPass;
    }

    public int getMaxConsecutiveFails() {
        return maxConsecutiveFails;
    }
}
