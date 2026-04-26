package com.towerdefense.ui;

import javafx.scene.paint.Color;

class Projectile {

    final Color color;
    private final double fromX, fromY, toX, toY;
    private double progress = 0.0;

    Projectile(double fromX, double fromY, double toX, double toY, Color color) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX   = toX;
        this.toY   = toY;
        this.color = color;
    }

    void advance(double fraction) {
        progress = Math.min(1.0, progress + fraction);
    }

    boolean isDone() { return progress >= 1.0; }

    double x() { return fromX + (toX - fromX) * progress; }
    double y() { return fromY + (toY - fromY) * progress; }
}

