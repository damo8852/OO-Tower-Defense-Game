package com.towerdefense.eventbus;

public interface ITowerDefenseEvents {
    // add observer for tower defense events
    void attach(ITowerDefenseObserver observer);
}
