package sagrada.util;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.Game;

import java.sql.SQLException;

public class ManageGame {
    private final Game game;
    private final DatabaseConnection connection;

    public ManageGame(Game game, DatabaseConnection connection) {
        this.game = game;
        this.connection = connection;
    }

    public void setNextPlayerTurn() {
        var playerRepository = new PlayerRepository(this.connection);
        var currentPlayer = this.game.getPlayerTurn();
        var amountOfPlayers = this.game.getPlayers().size();

        var newSequenceNumber = (amountOfPlayers * 2) - currentPlayer.getSequenceNumber() + 1;
        currentPlayer.setSequenceNumber(newSequenceNumber);

        try {
            playerRepository.update(currentPlayer);
            this.game.setPlayerTurn(playerRepository.getNextGamePlayer(this.game));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
