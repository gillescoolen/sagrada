package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import sagrada.model.Die;

public class RoundTrackController {
    @FXML
    private Button roundNumber;
    @FXML
    private StackPane roundDie;
    @FXML
    private Rectangle dieRectangle;
    @FXML
    private Text dieValue;

    private final int round;
    private final Die die;

    public RoundTrackController(int round, Die die) {
        this.round = round;
        this.die = die;
    }

    @FXML
    protected void initialize() {
        if (this.die.getValue() != null && this.die.getColor() != null) {
            this.roundDie.setVisible(true);
            this.dieRectangle.setFill(Paint.valueOf(this.die.getColor().getColor()));
            this.dieValue.setText(this.die.getValue().toString());
        } else {
            this.roundNumber.setText(String.valueOf(this.round));
        }
    }
}
