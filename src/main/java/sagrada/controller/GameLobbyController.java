package sagrada.controller;

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


    public GameLobbyController(DatabaseConnection databaseConnection, Game game) {
        this.databaseConnection = databaseConnection;
        this.playerRepository = new PlayerRepository(this.databaseConnection);
        this.game = game;
    }

    @FXML
    protected void initialize() {
        this.btnInvite.setOnMouseClicked(e -> this.invitePlayer());
        this.btnStartGame.setOnMouseClicked(e -> this.startGame());

        this.fillList();
    }

    private void fillList() {
        try {
            List<Player> invited = this.playerRepository.getInvitedPlayers(this.game);

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

        if (!playerName.isBlank() && !playerName.isEmpty()) {
            Player player = new Player();
            AccountRepository accountRepository = new AccountRepository(this.databaseConnection);

            try {
                player.setAccount(accountRepository.findByUsername(playerName));
                player.setCurrentPlayer(false);
                player.setPrivateObjectiveCard(new PrivateObjectiveCard(Color.BLUE));
                player.setPlayStatus(PlayStatus.INVITED);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void startGame() {
        // do something
    }
}
