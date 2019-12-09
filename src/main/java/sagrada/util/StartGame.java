package sagrada.util;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.database.repositories.PublicObjectiveCardRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.FavorToken;
import sagrada.model.Game;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StartGame {
    private final Game game;
    private final PlayerRepository playerRepository;
    private final PublicObjectiveCardRepository publicObjectiveCardRepository;
    private final ToolCardRepository toolCardRepository;
    private final FavorTokenRepository favorTokenRepository;

    private static final int FAVORTOKENAMOUNT = 24;

    public StartGame(Game game, DatabaseConnection databaseConnection) {
        this.game = game;
        this.playerRepository = new PlayerRepository(databaseConnection);
        this.publicObjectiveCardRepository = new PublicObjectiveCardRepository(databaseConnection);
        this.toolCardRepository = new ToolCardRepository(databaseConnection);
        this.favorTokenRepository = new FavorTokenRepository(databaseConnection);

        this.initializePlayers();
        this.initializeCards();
        this.initializeFavorTokens();
    }

    public Game getCreatedGame() {
        return this.game;
    }

    public void shareFavorTokens() {
        for (var player : this.game.getPlayers()) {
            var unUsedTokens = this.game.getFavorTokens().subList(0, player.getPatternCard().getDifficulty());

            try {
                this.favorTokenRepository.updatePlayerFavorTokens(player, unUsedTokens);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            unUsedTokens.forEach(favorToken -> favorToken.setPlayerId(player.getId()));
            player.addFavorTokens(unUsedTokens);
            this.game.removeFavorTokens(unUsedTokens);
        }
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

    private void initializeFavorTokens() {
        try {
            List<FavorToken> tokenList = new ArrayList<>();

            for (int i = 0; i < FAVORTOKENAMOUNT; i++) {
                tokenList.add(new FavorToken(0, 0, null, null));
            }

            this.favorTokenRepository.initializeFavorTokens(tokenList, this.game.getId());

            this.game.addFavorTokens(this.favorTokenRepository.getFavorTokens(this.game.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
