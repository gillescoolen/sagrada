package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sagrada.model.ToolCard;
import sagrada.model.card.activators.ToolCardActivator;

public class ToolCardController {
    @FXML
    private Text name;
    @FXML
    private Text description;
    @FXML
    private Text points;

    private final ToolCard toolCard;
    private final ToolCardActivator toolCardActivator;

    public ToolCardController(ToolCard toolCard, ToolCardActivator toolCardActivator) {
        this.toolCard = toolCard;
        this.toolCardActivator = toolCardActivator;
        this.toolCard.observe(this);
    }

    @FXML
    protected void initialize() {
        this.name.setText(this.toolCard.getName());
        this.description.setText(this.toolCard.getDescription());
        this.points.setText(Integer.toString(this.toolCard.getCost()));
        this.wrapper.setOnMouseClicked(event -> this.toolCardActivator.activate());
    }
}
