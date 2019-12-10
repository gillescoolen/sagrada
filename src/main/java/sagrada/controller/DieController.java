package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sagrada.model.Die;

public class DieController {
    @FXML
    private Button singleDie;

    private final Die die;
    private final GameController gameController;

    public DieController(Die die, GameController gameController) {
        this.die = die;
        this.gameController = gameController;
    }

    @FXML
    protected void initialize() {
        this.singleDie.setText(this.die.getValue().toString());
        this.singleDie.setStyle("-fx-background-color: " + this.die.getColor().getColor());

        this.singleDie.setOnMouseClicked(c -> this.selectDie());
    }

    private void selectDie() {
        this.gameController.setSelectedDie(this.die);
    }
}
