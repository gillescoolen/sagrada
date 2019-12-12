package sagrada.util;

import sagrada.database.DatabaseConnection;
import sagrada.model.Game;
import sagrada.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EndGame {
    private final Game game;
    private final DatabaseConnection connection;

    public EndGame(Game game, DatabaseConnection connection) {
        this.game = game;
        this.connection = connection;
    }

    public void correctAllPatternCards() {
        var updatedPlayerList = new ArrayList<Player>();
        var objectivePoints = new ArrayList<Integer>();
        var favorTokens = new ArrayList<Integer>();
        Player playerWithMostObjectivePoints;
        Player playerWithMostFavorTokens;

        for (var player : this.game.getPlayers()) {
            var points = 0;
            player.setPlayerFrame(player.validateFrameField(this.connection, this.game));

            for (var objectiveCard : this.game.getObjectiveCards()) {
                points += objectiveCard.calculatePoints(player.getPlayerFrame());
            }

            var privateObjectiveCardPoints = player.getPrivateObjectiveCard().calculatePoints(player.getPlayerFrame());
            var remainingFavorTokens = player.getFavorTokens().size();

            points += privateObjectiveCardPoints;
            points += remainingFavorTokens;
            points += player.getPlayerFrame().countEmptySquares();

            objectivePoints.add(privateObjectiveCardPoints);
            favorTokens.add(remainingFavorTokens);

            for (var objectivePoint : objectivePoints) {
                if (objectivePoint ) {

                }
            }

            for (var favorToken : favorTokens ) {

            }

            player.setScore(points);
            updatedPlayerList.add(player);
        }



        this.game.addPlayers(updatedPlayerList);
    }
}
