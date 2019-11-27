package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import sagrada.model.Account;
import sagrada.model.Game;

public class LobbyItemController {
    @FXML
    private Label lbName;
    @FXML
    private Label lbSpotsLeft;
    @FXML
    private AnchorPane lobbyItem;

    private final Game game;
    private final Account account;

    public LobbyItemController(Game game, Account account) {
        this.game = game;
        this.account = account;
    }

    @FXML
    protected void initialize() {
        this.lobbyItem.setId(Integer.toString(this.game.getId()));
        this.lbName.setText(this.game.getOwner().getAccount().getUsername() + "'s Game");
        this.lbSpotsLeft.setText(4 - this.game.getPlayers().size() + " spot(s) left");

        this.lobbyItem.setOnMouseClicked(c -> this.lobbyItemClicked());
    }

    private void lobbyItemClicked() {

    }

}
