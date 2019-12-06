package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import sagrada.model.ToolCard;

public class ToolCardController {
    @FXML
    private Text name;
    @FXML
    private Text description;
    @FXML
    private Text points;

    private final ToolCard toolCard;

    public ToolCardController(ToolCard toolCard) {
        this.toolCard = toolCard;
    }

    @FXML
    protected void initialize() {
        this.name.setText(this.toolCard.getName());
        this.description.setText(this.toolCard.getDescription());
        this.points.setText(Integer.toString(this.toolCard.getCost()));
    }
}
