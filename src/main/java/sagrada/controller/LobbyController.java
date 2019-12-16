package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyController {
    @FXML
    private VBox vbLobbyGames, vbLobbyInvites;
    @FXML
    private Button btnCreateGame, btnPreviousPage, btnNextPage, btnReverseOrder;

    private int page = 0;
    private int amountOfGames = 0;
    private boolean orderDesc = true;

    private final Account user;
    private final DatabaseConnection databaseConnection;
    private final Timer getGamesTimer = new Timer();
    private final Timer getInvitesTimer = new Timer();

    public LobbyController(DatabaseConnection databaseConnection, Account account) {
        this.databaseConnection = databaseConnection;
        this.user = account;
    }

    @FXML
    protected void initialize() {
        this.btnPreviousPage.setDisable(true);
        this.btnCreateGame.setOnMouseClicked(e -> this.createGame());
        this.btnPreviousPage.setOnMouseClicked(e -> this.previousPage());
        this.btnNextPage.setOnMouseClicked(e -> this.nextPage());
        this.btnReverseOrder.setOnMouseClicked(e -> this.reverseOrder());

        this.getGamesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getGames();
            }
        }, 0, 2000);
        this.getInvitesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                 getInvites();
            }
        }, 0, 2000);
    }

    private void getGames() {
        try {
            var gameRepository = new GameRepository(this.databaseConnection);
            var games = gameRepository.getAll(this.page * 20, this.orderDesc);
            var loader = this.getClass().getResource("/views/lobby/lobbyGame.fxml");
            this.fillLobbyList(games, this.vbLobbyGames, loader);
            this.amountOfGames = gameRepository.countAllGames();
            this.btnNextPage.setDisable(this.amountOfGames / 20 == this.page);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getInvites() {
        try {
            var gameRepository = new GameRepository(this.databaseConnection);
            var games = gameRepository.getInvitedGames(this.user);
            var loader = this.getClass().getResource("/views/lobby/lobbyInvite.fxml");
            this.fillLobbyList(games, this.vbLobbyInvites, loader);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillLobbyList(List<Game> games, VBox items, URL view) {
        Platform.runLater(() -> items.getChildren().clear());

        for (var game : games) {
            var loader = new FXMLLoader(view);
            loader.setController(new LobbyItemController(game, this.user, this.databaseConnection, this));

            Platform.runLater(() -> {
                try {
                    items.getChildren().add(loader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void createGame() {
        Player player = new Player();
        player.setAccount(this.user);
        player.setPlayStatus(PlayStatus.CHALLENGER);
        player.setPrivateObjectiveCard(new PrivateObjectiveCard(Color.BLUE));
        player.setCurrentPlayer(false);

        Game game = new Game();
        game.setCreatedOn(LocalDateTime.now());

        try {
            GameRepository gameRepository = new GameRepository(this.databaseConnection);
            gameRepository.add(game);

            game.setId(gameRepository.getLatestGameId());

            PlayerRepository playerRepository = new PlayerRepository(this.databaseConnection);
            playerRepository.add(player, game);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void stopTimers() {
        this.getGamesTimer.cancel();
        this.getGamesTimer.purge();
        this.getInvitesTimer.cancel();
        this.getInvitesTimer.purge();
    }

    private void nextPage() {
        this.page += 1;

        if (this.page != 0) {
            this.btnPreviousPage.setDisable(false);
        }
    }

    private void previousPage() {
        this.page -= 1;

        if (this.page == 0) {
            this.btnPreviousPage.setDisable(true);
        }
    }

    private void reverseOrder() {
        this.orderDesc = !this.orderDesc;
        this.btnReverseOrder.setText(this.orderDesc ? "Sorteer spellen van oud naar nieuw" : "Sorteer spellen van nieuw naar oud");
    }
}
