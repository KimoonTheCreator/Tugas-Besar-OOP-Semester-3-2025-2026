package org.example.manager;

/**
 * TimerManager untuk mengatur waktu dalam game.
 * Berfungsi sebagai central time keeper yang bisa mengakomodasi pause/resume di
 * masa depan.
 */
public class TimerManager {
    private static TimerManager instance;
    private long pausedTime = 0;
    private long startTime;
    private boolean isPaused = false;
    private long pauseStartTimestamp = 0;

    private TimerManager() {
        this.startTime = System.currentTimeMillis();
    }

    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    /**
     * Mendapatkan waktu game saat ini (dalam miliseconds sejak game mulai/di-reset)
     * Mengkoreksi waktu paused.
     */
    public long getCurrentTime() {
        if (isPaused) {
            return pauseStartTimestamp - startTime - pausedTime;
        }
        return System.currentTimeMillis() - startTime - pausedTime;
    }

    public void pause() {
        if (!isPaused) {
            isPaused = true;
            pauseStartTimestamp = System.currentTimeMillis();
        }
    }

    public void resume() {
        if (isPaused) {
            isPaused = false;
            long currentPauseDuration = System.currentTimeMillis() - pauseStartTimestamp;
            pausedTime += currentPauseDuration;
        }
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.pausedTime = 0;
        this.isPaused = false;
    }
}
