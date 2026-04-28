package com.towerdefense.eventbus;

public interface IGameObserver {
    // receive status message from event bus
    void update(String statusMessage);
}
