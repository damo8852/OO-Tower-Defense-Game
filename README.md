# Tower Defense OO Project

Our project is a tower defense game. Enemies march along a path on a grid map, players place and upgrade towers to stop them before lives run out.
The project demonstrates five classic OO design patterns throughout the architecture.

## Gameplay

- **Left-click** an empty cell to place the selected tower type
- **Left-click** an existing tower to upgrade it (costs 75g)
- **Right-click** a tower to sell it for half its cost
- Press **Start Wave** to send the next enemy wave
- Press **Undo** to reverse the last tower action
- Use **Save / Load** to persist and restore your game

Two map shapes are available: U-Shape and S-Shape. You can choose between them on the game-over screen.

## Design Patterns

### Factory — `EnemyFactory` and `TowerFactory`

Both factories follow the same abstract factory shape: a base class with an abstract create() method and concrete inner-class implementations.

**`EnemyFactory`** (`model/enemy/EnemyFactory.java`) — three inner factories produce the three enemy variants:

| Factory | Enemy | Notes |
|---|---|---|
| `Basic` | `BasicEnemy` | 100 HP base, scales +20 HP per wave |
| `Fast` | `FastEnemy` | 60 HP, moves 2 tiles per tick, rewards 15g |
| `Armored` | `ArmoredEnemy` | 200 HP, halves all incoming damage |

WaveSpawner receives a list of these factories and cycles through them to populate each wave.

TowerFactory (`model/tower/TowerFactory.java`)  the static `forType(TowerType)` method dispatches to the right inner factory, keeping placement code decoupled from concrete tower classes.

---

### Strategy — `TargetingStrategy`

**Interface** (`strategy/TargetingStrategy.java`):

```java
IEnemy select(List<IEnemy> inRange, Tile towerPosition);
```

Each tower holds a `TargetingStrategy` injected at construction time, so targeting behavior is swappable without touching tower logic.

| Strategy | Selects | Used by |
|---|---|---|
| `NearestEnemyStrategy` | Enemy closest to the tower | `BasicTower` |
| `StrongestEnemyStrategy` | Enemy with the most remaining HP | `SniperTower` |
| `FirstInPathStrategy` | Enemy furthest along the path | `SplashTower` |

`Tower.attackSingleTarget()` delegates the target selection entirely to the strategy — the tower only knows `select()` was called.

---

### Command — `GameCommand`

**Interface** (`command/GameCommand.java`):

```java
void execute();
void undo();
CommandRecord toRecord();   // for save/load serialisation
```

Three concrete commands cover all player tower actions:

| Command | `execute()` | `undo()` |
|---|---|---|
| `PlaceTowerCommand` | Adds tower, spends gold | Removes tower, refunds gold |
| `SellTowerCommand` | Removes tower, adds sell value | Re-adds tower, deducts sell value |
| `UpgradeTowerCommand` | Upgrades stats, spends 75g | Downgrades stats, refunds 75g |

`CommandHistory` maintains a stack of executed commands. Pressing **Undo** pops the top command and calls `undo()`. Each command also produces a `CommandRecord` (a simple data object) so the save service can replay the history on load.

---

### Observer — `EventBus`

**Singleton** (`eventbus/EventBus.java`) decouples `GameState` from the UI. Observers register with the bus, `GameState` fires typed events whenever its state changes.

**Event types** (`EventType`): `LIVES_CHANGED`, `SCORE_CHANGED`, `WAVE_CHANGED`, `GOLD_CHANGED`

Two observer interfaces exist:

- `IGameObserver` — receives a plain String status message
- `ITowerDefenseObserver` — receives a typed (EventType, Object) pair

`HudView` implements `ITowerDefenseObserver` and registers itself with the bus on creation. It detaches when a new game starts to avoid stale listeners. 

---

### Builder — `GameMap.Builder`

**`GameMap`** (`model/GameMap.java`) has a private constructor that only `Builder` can call, enforcing the pattern. 

```java
GameMap map = new GameMap.Builder()
    .size(10, 10)
    .shape(MapType.U_SHAPE)   // or MapType.S_SHAPE
    .build();
```

Convenience static methods hide common configurations:

```java
GameMap.Builder.defaultMap()      // 10×10 U-shape
GameMap.Builder.sShapeMap()       // 10×10 S-shape
GameMap.Builder.customMap(r, c)   // any size, U-shape path
```

Path tiles are recorded as ordered coordinates so enemies always traverse them in sequence.
