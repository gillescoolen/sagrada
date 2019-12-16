package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.model.Game;
import sagrada.model.Player;
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
    private Text favorTokensUsed;
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
        this.points.setText("K: " + this.toolCard.getCost());
        this.favorTokensUsed.setText("T: " + this.getAmountFavorTokensUsed());
    }

    @Override
    public void accept(ToolCard card) {
        if (card != null) {
            this.toolCard = card;

            if (this.toolCard.canUse() && !this.gameController.isToolCardUsed() && sufficientTokens()) {
                this.wrapper.getStyleClass().clear();
                this.wrapper.getStyleClass().add("tool-card-wrapper");
                this.wrapper.setOnMouseClicked(event -> this.useToolCard());
            } else {
                this.wrapper.getStyleClass().clear();
                this.wrapper.getStyleClass().add("tool-card-wrapper-disabled");
                this.wrapper.setOnMouseClicked(null);
            }

            this.points.setText("K: " + this.toolCard.getCost());
            this.favorTokensUsed.setText("T: " + this.getAmountFavorTokensUsed());
        }
    }

    private int getAmountFavorTokensUsed() {
        Game game = this.gameController.getGame();

        int amount = 0;

        try {
            amount = this.repository.getFavorTokensUsed(game.getId(), this.toolCard.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amount;
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
            var used = this.toolCardActivator.activate();
            this.gameController.setUsedToolCard(used);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
