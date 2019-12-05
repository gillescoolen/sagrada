package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
        this.btnInvite.setOnMouseClicked(e -> this.invitePlayer());
        this.btnStartGame.setOnMouseClicked(e -> this.startGame());

        this.getInvitedAndAcceptedPlayersTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> fillList(lvInvitedPlayers, true));
                Platform.runLater(() -> fillList(lvAcceptedPlayers, false));
            }
        }, 0, POLL_TIME);
    }

    private void fillList(ListView<String> listView, boolean invited) {
        try {
            listView.getItems().clear();
            List<Player> players;

            if (invited) {
                players = this.playerRepository.getInvitedPlayers(this.game);
            } else {
                players = this.playerRepository.getAcceptedPlayers(this.game);

                if (players.size() != 0) {
                    this.btnStartGame.setDisable(false);
                } else {
                    this.btnStartGame.setDisable(true);
                }
            }

            for (Player player : players) {
                listView.getItems().add(player.getAccount().getUsername());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void invitePlayer() {
        String playerName = this.tfPlayerInvite.getText();
        this.tfPlayerInvite.clear();

        if (playerName.isBlank() && playerName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning dialog");
            alert.setContentText("Please input the name of the player \nyou want to invite.");

            alert.showAndWait();

            return;
        }

        Player player = new Player();
        AccountRepository accountRepository = new AccountRepository(this.databaseConnection);

        try {
            Account account = accountRepository.findByUsername(playerName);

            if (account == null) {
                throw new SQLException("Account not found.");
            }

            if (account.getUsername().equals(this.account.getUsername())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning dialog");
                alert.setContentText("You can't invite yourself!");

                alert.showAndWait();

                return;
            }

            if (this.lvInvitedPlayers.getItems().contains(playerName) || this.lvAcceptedPlayers.getItems().contains(playerName)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning dialog");
                alert.setContentText("Could not invite the player! \nPlayer is already invited or already accepted the\ninvite.");

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
            alert.setContentText("Could not invite the player! \nConnection not found or player not found.");

            alert.showAndWait();

            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        if (game.getPlayers().size() > 2) {
            try {
                var loader = new FXMLLoader(getClass().getResource("/views/game.fxml"));
                var stage = ((Stage) this.btnInvite.getScene().getWindow());
                loader.setController(new GameController(this.databaseConnection, this.game));
                var scene = new Scene(loader.load());
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
