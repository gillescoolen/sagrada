package sagrada.util;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerRepository;
import sagrada.database.repositories.PublicObjectiveCardRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.Game;

import java.sql.SQLException;

public class StartGame {
    private final Game game;
    private final PlayerRepository playerRepository;
    private final PublicObjectiveCardRepository publicObjectiveCardRepository;
    private final ToolCardRepository toolCardRepository;

    public StartGame(Game game, DatabaseConnection databaseConnection) {
        this.game = game;
        this.playerRepository = new PlayerRepository(databaseConnection);
        this.publicObjectiveCardRepository = new PublicObjectiveCardRepository(databaseConnection);
        this.toolCardRepository = new ToolCardRepository(databaseConnection);

        this.initializePlayers();
        this.initializeCards();
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
        try {
            var publicObjectiveCards = this.publicObjectiveCardRepository.getRandom();
            this.publicObjectiveCardRepository.addMultiple(publicObjectiveCards, this.game.getId());

            var toolCards = this.toolCardRepository.getRandom();
            this.toolCardRepository.addMultiple(toolCards, this.game.getId());

            for (var publicObjectiveCard : publicObjectiveCards) {
                this.game.addObjectiveCard(publicObjectiveCard);
            }

            for (var toolCard : toolCards) {
                this.game.addToolCard(toolCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
