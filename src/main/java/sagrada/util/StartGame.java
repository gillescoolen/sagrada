package sagrada.util;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.Game;

import java.sql.SQLException;

public class StartGame {
    private final Game game;
    private final PlayerRepository playerRepository;

    public StartGame(Game game, DatabaseConnection databaseConnection) {
        this.game = game;
        this.playerRepository = new PlayerRepository(databaseConnection);
        this.initializePlayers();
    }

    public Game getCreatedGame() {
        return this.game;
    }

    private void initializePlayers() {
        try {
            this.game.addPlayers(this.playerRepository.prepareAllGamePlayers(this.game));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeCards() {

    }
}
