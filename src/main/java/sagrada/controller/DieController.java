package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sagrada.model.Die;

public class DieController {
    @FXML
    private Button singleDie;

    private final Die die;

    public DieController(Die die) {
        this.die = die;
    }

    @FXML
    protected void initialize() {
        this.singleDie.setText(this.die.getValue().toString());
        this.singleDie.setStyle("-fx-background-color: " + this.die.getColor().getColor());
    }
}
