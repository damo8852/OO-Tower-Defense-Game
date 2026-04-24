package com.towerdefense.eventbus;

import java.util.ArrayList;
import java.util.List;

public class EventBus implements IGame, ITowerDefenseEvents {

    private final List<IGameObserver> gameObservers = new ArrayList<>();
    private final List<ITowerDefenseObserver> towerDefenseObservers = new ArrayList<>();

    private static EventBus instance;

    private EventBus() {}

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    @Override
    public void attach(IGameObserver observer) {
        gameObservers.add(observer);
    }

    @Override
    public void detach(IGameObserver observer) {
        gameObservers.remove(observer);
    }

    @Override
    public void attach(ITowerDefenseObserver observer) {
        towerDefenseObservers.add(observer);
    }

    public void detach(ITowerDefenseObserver observer) {
        towerDefenseObservers.remove(observer);
    }

    public void postEvent(String statusMessage) {
        for (IGameObserver observer : gameObservers) {
            observer.update(statusMessage);
        }
    }

    public void postEvent(EventType eventType, Object eventObject) {
        for (ITowerDefenseObserver observer : towerDefenseObservers) {
            observer.update(eventType, eventObject);
        }
    }

    public static void resetForTesting() {
        instance = null;
    }
}
