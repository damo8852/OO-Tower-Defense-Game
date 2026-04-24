package com.towerdefense.eventbus;

public interface IGame {
    void attach(IGameObserver observer);
    void detach(IGameObserver observer);
}
