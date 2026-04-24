package com.towerdefense.eventbus;

public interface ITowerDefenseObserver {
    void update(EventType eventType, Object eventObject);
}
