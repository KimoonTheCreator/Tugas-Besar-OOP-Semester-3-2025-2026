package org.example.util;

import org.example.manager.TimerManager;

/**
 * Utility class untuk membuat timer/cooldown individual.
 * Menggunakan TimerManager sebagai referensi waktu.
 */
public class GameTimer {
    private long duration;
    private long startTime;
    private boolean isRunning;

    public GameTimer(long durationMs) {
        this.duration = durationMs;
        this.isRunning = false;
        // Set startTime jauh di belakang agar langsung ready saat pertama kali
        this.startTime = -durationMs;
    }

    public void start() {
        this.startTime = TimerManager.getInstance().getCurrentTime();
        this.isRunning = true;
    }

    public boolean isReady() {
        return TimerManager.getInstance().getCurrentTime() - startTime >= duration;
    }

    public long getRemaining() {
        long elapsed = TimerManager.getInstance().getCurrentTime() - startTime;
        return Math.max(0, duration - elapsed);
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
