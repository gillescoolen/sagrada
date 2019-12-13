package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sagrada.component.BackButton;
import sagrada.component.GameScreen;
import sagrada.component.LobbyScreen;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.AccountRepository;
import sagrada.database.repositories.GameRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameLobbyCreatorController {
    @FXML
    private AnchorPane apPanel;
    @FXML
    private VBox vbPanel;
    @FXML
    private TextField tfPlayerInvite;
    @FXML
    private Button btnInvite;
    @FXML
    private Button btnStartGame;
    @FXML
    private ListView<String> lvInvitedPlayers;
    @FXML
    private ListView<String> lvAcceptedPlayers;

    private final DatabaseConnection databaseConnection;
    private final PlayerRepository playerRepository;
    private final Account account;
    private final Game game;
    private final static int POLL_TIME = 3000;
    private final Timer getInvitedAndAcceptedPlayersTimer = new Timer();

    public GameLobbyCreatorController(DatabaseConnection databaseConnection, Game game, Account account) {
        this.databaseConnection = databaseConnection;
        this.playerRepository = new PlayerRepository(this.databaseConnection);
        this.account = account;
        this.game = game;
    }

    @FXML
    protected void initialize() {
        this.addBackButton();

        this.btnInvite.setOnMouseClicked(e -> this.invitePlayer());
        this.btnStartGame.setOnMouseClicked(e -> this.startGame());

        this.getInvitedAndAcceptedPlayersTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                fillList(lvInvitedPlayers, true);
                fillList(lvAcceptedPlayers, false);
            }
        }, 0, POLL_TIME);
    }

    private void addBackButton() {
        try {
            this.vbPanel.getChildren().add(0, new BackButton(this::backToLobbyScreen).load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backToLobbyScreen() {
        try {
            var stage = ((Stage) this.apPanel.getScene().getWindow());
            var scene = new Scene(new LobbyScreen(this.databaseConnection, this.account).load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillList(ListView<String> listView, boolean invited) {
        try {

            List<Player> players;

            if (invited) {
                players = this.playerRepository.getInvitedPlayers(this.game);
            } else {
                players = this.playerRepository.getAcceptedPlayers(this.game);
                this.game.addPlayers(this.playerRepository.getAllGamePlayers(this.game));

                Platform.runLater(() -> {
                    if (players.size() != 0) {
                        this.btnStartGame.setDisable(false);
                    } else {
                        this.btnStartGame.setDisable(true);
                    }
                });
            }

            Platform.runLater(() -> {
                listView.getItems().clear();
                for (Player player : players) {
                    listView.getItems().add(player.getAccount().getUsername());
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void invitePlayer() {
        String playerName = this.tfPlayerInvite.getText();
        this.tfPlayerInvite.clear();

        if (playerName.isBlank() && playerName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Waarschuwings dialoog");
            alert.setContentText("Vul alstublieft de naam van de speler in \ndie u wilt uitnodigen.");

            alert.showAndWait();

            return;
        }

        Player player = new Player();
        AccountRepository accountRepository = new AccountRepository(this.databaseConnection);

        try {
            Account account = accountRepository.findByUsername(playerName);

            if (account == null) {
                throw new SQLException("Account niet gevonden.");
            }

            if (account.getUsername().equals(this.account.getUsername())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Waarschuwings dialoog");
                alert.setContentText("U kan uzelf niet uitnodigen!");

                alert.showAndWait();

                return;
            }

            if (this.lvInvitedPlayers.getItems().contains(playerName) || this.lvAcceptedPlayers.getItems().contains(playerName)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Waarschuwings dialoog");
                alert.setContentText("Kon de speler niet uitnodigen! \nDe speler is al uitgenodigd, of heeft al\ngeaccepteerd.");

                alert.showAndWait();

                return;
            }

            player.setAccount(account);
            player.setCurrentPlayer(false);
            player.setPrivateObjectiveCard(new PrivateObjectiveCard(Color.BLUE));
            player.setPlayStatus(PlayStatus.INVITED);

            this.playerRepository.add(player, this.game);

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error dialog");
            alert.setContentText("Kon de speler niet uitnodigen! \nConnectie of speler niet gevonden.");

            alert.showAndWait();

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        if (this.game.getPlayers().size() >= 2) {
            try {
                var stage = ((Stage) this.btnInvite.getScene().getWindow());
                var scene = new Scene(new GameScreen(this.databaseConnection, this.game, this.account).load());

                var gameRepository = new GameRepository(this.databaseConnection);

                gameRepository.startGame(this.game.getOwner().getId(), this.game.getId());

                stage.setScene(scene);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
