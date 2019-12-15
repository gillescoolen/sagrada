package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import sagrada.component.GameLobbyCreatorScreen;
import sagrada.component.GameLobbyPlayerScreen;
import sagrada.component.GameScreen;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    private final LobbyController lobbyController;

    public LobbyItemController(Game game, Account account, DatabaseConnection connection, LobbyController lobbyController) {
        this.game = game;
        this.account = account;
        this.databaseConnection = connection;
        this.lobbyController = lobbyController;
    }

    @FXML
    protected void initialize() {
        this.fillItem();
        this.bindButtons();
    }

    private void fillItem() {
        if (this.game.getOwner() != null) this.lbName.setText(this.game.getOwner().getAccount().getUsername() + "'s Spel");

        GameRepository gameRepository = new GameRepository(this.databaseConnection);

        int spots = this.getSpots(this.game.getPlayers());
        this.lbSpotsLeft.setText(spots + " plek(ken) over!");

        try {
            if (containsFinished(this.game.getPlayers())) {

                var list = this.game.getPlayers().stream()
                        .sorted(Comparator.comparingInt(Player::getScore))
                        .collect(Collectors.toList());

                this.lbName.setText(list.get(1).getAccount().getUsername() + "'s gewonnen spel");
                this.lbSpotsLeft.setText("Spel is afgerond");

                this.lobbyItem.setDisable(true);
                this.lobbyItem.getStyleClass().clear();
                this.lobbyItem.getStyleClass().add("item-finished");
            } else {
                if (gameRepository.checkIfGameHasStarted(this.game) && !this.containsName(this.game.getPlayers(), this.account.getUsername())) {
                    this.lbSpotsLeft.setText("Spel is begonnen");

                    this.lobbyItem.setDisable(true);
                    this.lobbyItem.getStyleClass().clear();
                    this.lobbyItem.getStyleClass().add("item-full");

                    if (this.btnDecline != null) this.btnDecline.setDisable(true);
                } else if (gameRepository.checkIfGameHasStarted(this.game) && this.containsName(this.game.getPlayers(), this.account.getUsername())) {
                    this.lbSpotsLeft.setText("Spel is begonnen");

                    this.lobbyItem.getStyleClass().clear();
                    this.lobbyItem.getStyleClass().add("item-started");

                    if (this.btnDecline != null) this.btnDecline.setDisable(true);
                } else {
                    if (spots == 0 || !this.containsName(this.game.getPlayers(), this.account.getUsername())) {
                        this.lobbyItem.setDisable(true);
                        this.lobbyItem.getStyleClass().clear();
                        this.lobbyItem.getStyleClass().add("item-full");
                    } else if (this.containsNameAndAccepted(this.game.getPlayers(), this.account.getUsername())) {
                        this.lobbyItem.getStyleClass().clear();
                        this.lobbyItem.getStyleClass().add("item-accepted");

                        if (this.btnDecline != null) this.btnDecline.setDisable(true);
                    } else if (!this.containsNameAndAccepted(this.game.getPlayers(), this.account.getUsername())) {
                        this.lobbyItem.setDisable(false);
                        this.lobbyItem.getStyleClass().clear();
                        this.lobbyItem.getStyleClass().add("item");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void bindButtons() {
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
        this.lobbyController.stopTimers();

        try {
            FXMLLoader loader;
            GameRepository gameRepository = new GameRepository(this.databaseConnection);
            if (this.account.getUsername().equals(this.game.getOwner().getAccount().getUsername())) {
                if (gameRepository.checkIfGameHasStarted(this.game)) {
                    loader = new GameScreen(this.databaseConnection, this.game, this.account);
                } else {
                    loader = new GameLobbyCreatorScreen(this.databaseConnection, this.game, this.account);
                }
            } else {
                if (gameRepository.checkIfGameHasStarted(this.game)) {
                    loader = new GameScreen(this.databaseConnection, this.game, this.account);
                } else {
                    if (!this.containsNameAndAccepted(this.game.getPlayers(), this.account.getUsername())) {
                        PlayerRepository playerRepository = new PlayerRepository(this.databaseConnection);
                        playerRepository.acceptInvite(this.account.getUsername(), this.game);
                    }

                    loader = new GameLobbyPlayerScreen(this.databaseConnection, this.game, this.account);
                }
            }

            var stage = ((Stage) this.lbName.getScene().getWindow());
            var scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean containsName(final List<Player> players, final String name) {
        return players.stream().anyMatch(p -> p.getAccount().getUsername().equals(name));
    }

    private boolean containsNameAndAccepted(final List<Player> players, final String name) {
        return players.stream().anyMatch(p -> p.getAccount().getUsername().equals(name) && (p.getPlayStatus() == PlayStatus.ACCEPTED || p.getPlayStatus() == PlayStatus.CHALLENGER));
    }

    private boolean containsFinished(final List<Player> players) {
        return players.stream().anyMatch(p -> (p.getPlayStatus() == PlayStatus.DONE_PLAYING));
    }

    private int getSpots(List<Player> players) {
        return MAX_PLAYERS - (int) players.stream()
                .filter(p -> p.getPlayStatus() == PlayStatus.ACCEPTED || p.getPlayStatus() == PlayStatus.CHALLENGER)
                .count();
    }
}
