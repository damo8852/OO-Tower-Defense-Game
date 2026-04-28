package com.towerdefense.eventbus;

public interface IGame {
    // add observer for game status
    void attach(IGameObserver observer);
    // remove observer for game status
    void detach(IGameObserver observer);
}
