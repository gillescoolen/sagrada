package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import sagrada.database.repositories.FavorTokenRepository;
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

    private ToolCard toolCard;
    private final ToolCardActivator toolCardActivator;
    private final GameController gameController;
    private final FavorTokenRepository repository;

    public ToolCardController(GameController gameController, FavorTokenRepository repository, ToolCard toolCard, ToolCardActivator toolCardActivator) {
        this.toolCard = toolCard;
        this.toolCardActivator = toolCardActivator;
        this.gameController = gameController;
        this.repository = repository;

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
            this.toolCard = card;
            this.points.setText(Integer.toString(this.toolCard.getCost()));

            if (this.toolCard.canUse() && !this.gameController.isToolCardUsed() && sufficientTokens()) {
                this.wrapper.getStyleClass().clear();
                this.wrapper.getStyleClass().add("tool-card-wrapper");
                this.wrapper.setOnMouseClicked(event -> this.useToolCard());
            } else {
                this.wrapper.getStyleClass().clear();
                this.wrapper.getStyleClass().add("tool-card-wrapper-disabled");
                this.wrapper.setOnMouseClicked(null);
            }
        }
    }

    private boolean sufficientTokens() {
        int totalPoints = 0;

        try {
            totalPoints = this.repository.getPlayerFavorTokensTotal(this.gameController.getGame().getId(), this.gameController.getPlayer().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalPoints >= this.toolCard.getCost();
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
