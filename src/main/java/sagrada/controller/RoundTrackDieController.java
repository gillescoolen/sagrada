package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import sagrada.model.Die;

public class RoundTrackDieController {
    @FXML
    private Button roundNumber;

    private Integer round;
    private Die die;

    public RoundTrackDieController(Die die) {
        this.die = die;
    }

    public RoundTrackDieController(int round) {
        this.round = round;
    }

    @FXML
    protected void initialize() {
        if (this.die != null) {
            this.roundNumber.setText(this.die.getValue().toString());
            this.roundNumber.setStyle("-fx-background-color: " + this.die.getColor().getColor() + "; -fx-background-radius: 0%;");
        } else {
            this.roundNumber.setText(this.round.toString());
        }
    }
}
