package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.AccountRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameLobbyController {
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
    private final Game game;
    private final Timer getInvitedAndAcceptedPlayersTimer = new Timer();

    public GameLobbyController(DatabaseConnection databaseConnection, Game game) {
        this.databaseConnection = databaseConnection;
        this.playerRepository = new PlayerRepository(this.databaseConnection);
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
        }, 0, 3000);
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
        if (!playerName.isBlank() && !playerName.isEmpty()) {
            Player player = new Player();
            AccountRepository accountRepository = new AccountRepository(this.databaseConnection);

            try {
                Account account = accountRepository.findByUsername(playerName);

                if (account == null) {
                    throw new SQLException("Account not found.");
                }

                if (this.lvInvitedPlayers.getItems().contains(playerName) || this.lvAcceptedPlayers.getItems().contains(playerName)) {
                    throw new Exception("Player is invited or accepted.");
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error dialog");
                alert.setContentText("Could not invite the player! \nPlayer is already invited or already accepted the\ninvite.");

                alert.showAndWait();

                e.printStackTrace();
            }
        }
    }

    private void startGame() {
        // TODO: GitHub issue #56 Start game from invite page
    }
}
