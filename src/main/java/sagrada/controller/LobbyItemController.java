package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

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

    public LobbyItemController(Game game, Account account, DatabaseConnection connection) {
        this.game = game;
        this.account = account;
        this.databaseConnection = connection;
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
        // If you are in a game just go to the game page.
        // Otherwise create player and go to game page.
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
        // do something
    }

    private boolean containsName(final List<Player> list, final String name) {
        return list.stream().filter(p -> p.getAccount().getUsername().equals(name)).findFirst().isPresent();
    }
}
