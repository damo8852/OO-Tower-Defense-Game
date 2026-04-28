package com.towerdefense.eventbus;

public interface ITowerDefenseObserver {
    // receive typed game event from event bus
    void update(EventType eventType, Object eventObject);
}
