package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sagrada.model.ToolCard;
import sagrada.model.card.activators.ToolCardActivator;

import java.util.function.Consumer;

public class ToolCardController implements Consumer<ToolCard> {
    @FXML
    private Text name;
    @FXML
    private Text description;
    @FXML
    private Text points;
    @FXML
    private VBox wrapper;

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

    @Override
    public void accept(ToolCard card) {
        if (card != null) {
            this.points.setText(Integer.toString(toolCard.getCost()));
        }
    }
}
