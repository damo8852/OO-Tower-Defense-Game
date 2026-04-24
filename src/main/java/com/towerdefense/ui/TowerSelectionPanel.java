package com.towerdefense.ui;

import com.towerdefense.model.Tile;
import com.towerdefense.model.TowerType;
import com.towerdefense.model.tower.Tower;
import com.towerdefense.model.tower.TowerFactory;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import java.util.LinkedHashMap;
import java.util.Map;

public class TowerSelectionPanel extends VBox {

    private static final int SPACING = 4;

    private final ToggleGroup group = new ToggleGroup();
    private final Map<ToggleButton, TowerType> typeMap = new LinkedHashMap<>();

    public TowerSelectionPanel() {
        super(SPACING);
        getChildren().add(new Label("Select Tower:"));
        addButton(TowerType.BASIC,  "Basic Tower  [B]");
        addButton(TowerType.SNIPER, "Sniper Tower [S]");
        addButton(TowerType.SPLASH, "Splash Tower [X]");
    }

    private void addButton(TowerType type, String displayName) {
        String label = displayName + " (" + type.getCost() + "g)";
        ToggleButton btn = new ToggleButton(label);
        btn.setToggleGroup(group);
        btn.setMaxWidth(Double.MAX_VALUE);
        typeMap.put(btn, type);
        getChildren().add(btn);
    }

    public Tower createTower(Tile cell) {
        if (group.getSelectedToggle() == null) return null;
        TowerType type = typeMap.get((ToggleButton) group.getSelectedToggle());
        return TowerFactory.forType(type).create(cell);
    }
}
