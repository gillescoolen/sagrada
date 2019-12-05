package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LobbyItemController {
    @FXML
    private Label lbName;
    @FXML
    private Label lbSpotsLeft;
    @FXML
    private AnchorPane lobbyItem;
    @FXML
    private Button btnDecline;

    private final Game game;
    private final Account account;
    private final DatabaseConnection databaseConnection;
    private final static int MAX_PLAYERS = 4;

    public LobbyItemController(Game game, Account account, DatabaseConnection connection) {
        this.game = game;
        this.account = account;
        this.databaseConnection = connection;
    }

    @FXML
    protected void initialize() {
        this.lbName.setText(this.game.getOwner().getAccount().getUsername() + "'s Game");

        int spots = this.getSpots(this.game.getPlayers());
        this.lbSpotsLeft.setText(spots + " spot(s) left!");

        if (this.containsNameAndAccepted(this.game.getPlayers(), this.account.getUsername())) {
            this.lobbyItem.getStyleClass().clear();
            this.lobbyItem.getStyleClass().add("item-accepted");
            if (this.btnDecline != null) this.btnDecline.setDisable(true);
        } else if (spots == 0 || !this.containsName(this.game.getPlayers(), this.account.getUsername())) {
            this.lobbyItem.setDisable(true);
            this.lobbyItem.getStyleClass().clear();
            this.lobbyItem.getStyleClass().add("item-full");
        }

        this.lobbyItem.setOnMouseClicked(c -> this.lobbyItemClicked());

        if (this.btnDecline != null) this.btnDecline.setOnMouseClicked(c -> this.cancelInvite());
    }

    private void lobbyItemClicked() {
        goToNextScreen();
    }

    private void cancelInvite() {
        try {
            PlayerRepository playerRepository = new PlayerRepository(this.databaseConnection);
            playerRepository.declineInvite(this.account.getUsername(), this.game);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void goToNextScreen() {
        try {
            if (this.account.getUsername().equals(this.game.getOwner().getAccount().getUsername())) {
                var loader = new FXMLLoader(getClass().getResource("/views/lobby/gameLobbyCreator.fxml"));
                loader.setController(new GameLobbyCreatorController(this.databaseConnection, this.game, this.account));
                var stage = ((Stage) this.lbName.getScene().getWindow());
                var scene = new Scene(loader.load());
                stage.setScene(scene);
            } else {
                if (!this.containsNameAndAccepted(this.game.getPlayers(), this.account.getUsername())) {
                    PlayerRepository playerRepository = new PlayerRepository(this.databaseConnection);
                    playerRepository.acceptInvite(this.account.getUsername(), this.game);
                }

                var loader = new FXMLLoader(getClass().getResource("/views/lobby/gameLobbyPlayer.fxml"));
                loader.setController(new GameLobbyPlayerController(this.databaseConnection, this.game, account));
                var stage = ((Stage) this.lbName.getScene().getWindow());
                var scene = new Scene(loader.load());
                stage.setScene(scene);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean containsName(final List<Player> players, final String name) {
        return players.stream().anyMatch(p -> p.getAccount().getUsername().equals(name));
    }

    private boolean containsNameAndAccepted(final List<Player> players, final String name) {
        return players.stream().anyMatch(p -> p.getAccount().getUsername().equals(name) && p.getPlayStatus() == PlayStatus.ACCEPTED);
    }

    private int getSpots(List<Player> players) {
        return MAX_PLAYERS - (int)players.stream()
                .filter(p -> p.getPlayStatus() == PlayStatus.ACCEPTED || p.getPlayStatus() == PlayStatus.CHALLENGER)
                .count();
    }
}
