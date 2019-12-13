package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import sagrada.model.PublicObjectiveCard;
import sagrada.model.ToolCard;

public class PublicObjectiveCardController {
    @FXML
    private Text name;
    @FXML
    private Text description;
    @FXML
    private Text points;

    private PublicObjectiveCard publicObjectiveCard;
    private ToolCard toolCard;

    public PublicObjectiveCardController(PublicObjectiveCard publicObjectiveCard) {
        this.publicObjectiveCard = publicObjectiveCard;
    }

    public PublicObjectiveCardController(ToolCard toolCard) {
        this.toolCard = toolCard;
    }

    @FXML
    protected void initialize() {
        if (this.publicObjectiveCard == null) {
            this.name.setText(this.toolCard.getName());
            this.description.setText(this.toolCard.getDescription());
            this.points.setText(Integer.toString(this.toolCard.getCost()));
        } else {
            this.name.setText(this.publicObjectiveCard.getName());
            this.description.setText(this.publicObjectiveCard.getDescription());
            this.points.setText(Integer.toString(this.publicObjectiveCard.getPoints()));
        }
    }
}
