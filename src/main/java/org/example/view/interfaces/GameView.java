package org.example.view.interfaces;

public interface GameView {
    void printMap(char[][] mapLayout);
    void showScore(int score);
    void showTimer(int seconds);
    void showMessage(String message);
}