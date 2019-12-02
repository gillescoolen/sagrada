package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

import java.sql.SQLException;

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

    public LobbyItemController(Game game, Account account, DatabaseConnection connection) {
        this.game = game;
        this.account = account;
        this.databaseConnection = connection;
    }

    @FXML
    protected void initialize() {
        this.lobbyItem.setId(Integer.toString(this.game.getId()));
        this.lbName.setText(this.game.getOwner().getAccount().getUsername() + "'s Game");
        this.lbSpotsLeft.setText(MAX_PLAYERS - this.game.getPlayers().size() + " spot(s) left!");

        this.lobbyItem.setOnMouseClicked(c -> this.lobbyItemClicked());
    }

    private void lobbyItemClicked() {
        PlayerRepository playerRepository = new PlayerRepository(this.databaseConnection);
        Player player = new Player();

        player.setAccount(this.account);
        player.setCurrentPlayer(false);
        player.setPrivateObjectiveCard(new PrivateObjectiveCard(Color.BLUE));
        player.setPlayStatus(PlayStatus.ACCEPTED);

        try {
            playerRepository.add(player, this.game);
            System.out.println("You joined game " + this.game.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
