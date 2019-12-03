package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.AccountRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameLobbyController {
    @FXML
    private TextField tfPlayerInvite;
    @FXML
    private Button btnInvite;
    @FXML
    private ListView<String> lvInvitedPlayers;
    @FXML
    private Button btnStartGame;

    private final DatabaseConnection databaseConnection;
    private final PlayerRepository playerRepository;
    private final Game game;
    private final Timer getInvitedPlayersTimer = new Timer();


    public GameLobbyController(DatabaseConnection databaseConnection, Game game) {
        this.databaseConnection = databaseConnection;
        this.playerRepository = new PlayerRepository(this.databaseConnection);
        this.game = game;
    }

    @FXML
    protected void initialize() {
        this.btnInvite.setOnMouseClicked(e -> this.invitePlayer());
        this.btnStartGame.setOnMouseClicked(e -> this.startGame());

        this.getInvitedPlayersTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> fillInvitedPlayerList());
            }
        }, 0, 3000);
    }

    private void fillInvitedPlayerList() {
        try {
            this.lvInvitedPlayers.getItems().clear();
            List<Player> invited = this.playerRepository.getInvitedPlayers(this.game);

            if (invited.size() != 0) {
                this.btnStartGame.setDisable(false);
            } else {
                this.btnStartGame.setDisable(true);
            }

            for (Player player : invited) {
                this.lvInvitedPlayers.getItems().add(player.getAccount().getUsername());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void invitePlayer() {
        String playerName = this.tfPlayerInvite.getText();
        this.tfPlayerInvite.clear();
        System.out.println("Hallo");
        if (!playerName.isBlank() && !playerName.isEmpty()) {
            Player player = new Player();
            AccountRepository accountRepository = new AccountRepository(this.databaseConnection);

            try {
                player.setAccount(accountRepository.findByUsername(playerName));
                player.setCurrentPlayer(false);
                player.setPrivateObjectiveCard(new PrivateObjectiveCard(Color.BLUE));
                player.setPlayStatus(PlayStatus.INVITED);

                this.playerRepository.add(player, this.game);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void startGame() {
        // do something
    }
}
