package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LobbyItemController {
    @FXML
    private Label lbName;
    @FXML
    private Label lbSpotsLeft;
    @FXML
    private AnchorPane lobbyItem;

    private final Game game;
    private final Account account;
    private final DatabaseConnection databaseConnection;
    private final static int MAX_PLAYERS = 4;
    private final boolean isInvite;

    public LobbyItemController(Game game, Account account, DatabaseConnection connection, boolean isInvite) {
        this.game = game;
        this.account = account;
        this.databaseConnection = connection;
        this.isInvite = isInvite;
    }

    @FXML
    protected void initialize() {
        this.lbName.setText(this.game.getOwner().getAccount().getUsername() + "'s Game");

        int spots = MAX_PLAYERS - this.game.getPlayers().size();
        this.lbSpotsLeft.setText(spots + " spot(s) left!");

        if (spots == 0 || !this.containsName(this.game.getPlayers(), this.account.getUsername())) {
            this.lobbyItem.setDisable(true);
            this.lobbyItem.getStyleClass().clear();
            this.lobbyItem.getStyleClass().add("item-full");
        }

        this.lobbyItem.setOnMouseClicked(c -> this.lobbyItemClicked());
    }

    private void lobbyItemClicked() {
        if (this.containsName(this.game.getPlayers(), this.account.getUsername())) {
            this.goToGame();
        } else {
            PlayerRepository playerRepository = new PlayerRepository(this.databaseConnection);
            Player player = new Player();

            player.setAccount(this.account);
            player.setCurrentPlayer(false);
            player.setPrivateObjectiveCard(new PrivateObjectiveCard(Color.BLUE));
            player.setPlayStatus(PlayStatus.ACCEPTED);

            try {
                playerRepository.add(player, this.game);
                this.goToGame();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void goToGame() {
        try {
            var loader = new FXMLLoader(getClass().getResource("/views/game.fxml"));
            var stage = ((Stage) this.lbName.getScene().getWindow());
            loader.setController(new GameController(this.databaseConnection, this.game));
            var scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean containsName(final List<Player> players, final String name) {
        return players.stream().anyMatch(p -> p.getAccount().getUsername().equals(name));
    }
}
