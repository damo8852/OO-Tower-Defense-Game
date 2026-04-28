package com.towerdefense.eventbus;

import java.util.ArrayList;
import java.util.List;

public class EventBus implements IGame, ITowerDefenseEvents {

    private final List<IGameObserver> gameObservers = new ArrayList<>();
    private final List<ITowerDefenseObserver> towerDefenseObservers = new ArrayList<>();

    private static EventBus instance;

    private EventBus() {}

    // return singleton instance, create if needed
    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    // add a game status observer
    @Override
    public void attach(IGameObserver observer) {
        gameObservers.add(observer);
    }

    // remove a game status observer
    @Override
    public void detach(IGameObserver observer) {
        gameObservers.remove(observer);
    }

    // add a typed tower defense observer
    @Override
    public void attach(ITowerDefenseObserver observer) {
        towerDefenseObservers.add(observer);
    }

    // remove a typed tower defense observer
    public void detach(ITowerDefenseObserver observer) {
        towerDefenseObservers.remove(observer);
    }

    // broadcast status string to all observers
    public void postEvent(String statusMessage) {
        for (IGameObserver observer : gameObservers) {
            observer.update(statusMessage);
        }
    }

    // broadcast typed event to all tower defense observers
    public void postEvent(EventType eventType, Object eventObject) {
        for (ITowerDefenseObserver observer : towerDefenseObservers) {
            observer.update(eventType, eventObject);
        }
    }

    // reset singleton for testing
    public static void resetForTesting() {
        instance = null;
    }
}
