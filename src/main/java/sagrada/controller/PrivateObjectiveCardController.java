package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import sagrada.model.Player;
import sagrada.model.PrivateObjectiveCard;

public class PrivateObjectiveCardController {
    @FXML
    private Text name;
    @FXML
    private Text description;
    @FXML
    private Rectangle color;

    private PrivateObjectiveCard privateObjectiveCard;

    public PrivateObjectiveCardController(Player player) {
        this.privateObjectiveCard = player.getPrivateObjectiveCard();
    }

    @FXML
    protected void initialize() {
        var color = this.privateObjectiveCard.getColor().getColor();

        this.name.setText("Shades of " + color);
        this.description.setText("Private Sum of values on " + color + " dice");
        this.color.setFill(Color.web(color));
    }
}
