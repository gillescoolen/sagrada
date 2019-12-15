package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import sagrada.model.ToolCard;
import sagrada.model.card.activators.ToolCardActivator;

import java.sql.SQLException;
import java.util.function.Consumer;

public class ToolCardController implements Consumer<ToolCard> {
    @FXML
    private Text name;
    @FXML
    private Text description;
    @FXML
    private Text points;
    @FXML
    private AnchorPane wrapper;

    private final ToolCard toolCard;
    private final ToolCardActivator toolCardActivator;
    private final GameController gameController;

    public ToolCardController(GameController gameController, ToolCard toolCard, ToolCardActivator toolCardActivator) {
        this.toolCard = toolCard;
        this.toolCardActivator = toolCardActivator;
        this.gameController = gameController;

        this.toolCard.observe(this);
    }

    @FXML
    protected void initialize() {
        this.name.setText(this.toolCard.getName());
        this.description.setText(this.toolCard.getDescription());
        this.points.setText(Integer.toString(this.toolCard.getCost()));
    }

    @Override
    public void accept(ToolCard card) {
        if (card != null) {
            this.points.setText(Integer.toString(card.getCost()));

            if (toolCard.canUse() && !this.gameController.isToolCardUsed()) {
                this.wrapper.getStyleClass().remove("tool-card-wrapper-disabled");

                this.wrapper.setOnMouseClicked(event -> this.useToolCard());
            } else {
                this.wrapper.getStyleClass().add("tool-card-wrapper-disabled");

                this.wrapper.setOnMouseClicked(null);
            }
        }
    }

    private void useToolCard() {
        try {
            this.toolCardActivator.activate();
            this.gameController.setUsedToolCard(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
