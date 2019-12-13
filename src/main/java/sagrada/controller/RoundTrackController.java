package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import sagrada.model.Die;

public class RoundTrackController {
    @FXML
    private Button singleRound;

    private final int round;
    private final Die die;

    public RoundTrackController(int round, Die die) {
        this.round = round;
        this.die = die;
    }

    @FXML
    protected void initialize() {
        this.singleRound.setText(String.valueOf(this.round));

        if (this.die.getValue() != null && this.die.getColor() != null) {
            Tooltip tooltip = new Tooltip();
            tooltip.setText("Kleur: " + this.die.getColor().getDutchColorName() + " " + this.die.getValue());
            this.singleRound.setTooltip(tooltip);
        }
    }
}
