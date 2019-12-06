package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import sagrada.model.PublicObjectiveCard;

public class PublicObjectiveCardController {
    @FXML
    private Text name;
    @FXML
    private Text description;
    @FXML
    private Text points;

    private final PublicObjectiveCard publicObjectiveCard;

    public PublicObjectiveCardController(PublicObjectiveCard publicObjectiveCard) {
        this.publicObjectiveCard = publicObjectiveCard;
    }

    @FXML
    protected void initialize() {
        this.name.setText(this.publicObjectiveCard.getName());
        this.description.setText(this.publicObjectiveCard.getDescription());
        this.points.setText(Integer.toString(this.publicObjectiveCard.getPoints()));
    }
}
