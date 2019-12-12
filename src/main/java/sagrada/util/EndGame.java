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

    public void calculatePoints() {
        var updatedPlayerList = new ArrayList<Player>();
        var tiePlayerList = new ArrayList<Player>();

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
            points -= player.getPlayerFrame().countEmptySquares();

            player.setScore(points);
            updatedPlayerList.add(player);
        }

        Player bestScoringPlayer = null;
        var highestScore = 0;
        for (var player : updatedPlayerList) {
            if (bestScoringPlayer == null) {
                bestScoringPlayer = player;
                highestScore = player.getScore();
            } else {
                if (highestScore == player.getScore()) {
                    if (!tiePlayerList.contains(bestScoringPlayer)) {
                        tiePlayerList.add(bestScoringPlayer);
                    }
                    tiePlayerList.add(player);
                } else if (highestScore < player.getScore()) {
                    bestScoringPlayer = player;
                    highestScore = player.getScore();
                }
            }
        }

        if (!tiePlayerList.isEmpty()) {
            var winner = getWinnerWhenTie(tiePlayerList);
            // TODO: add 1 point for the winner
        }

        this.game.addPlayers(updatedPlayerList);
    }

    private Player getWinnerWhenTie(ArrayList<Player> tiePlayerList) {
        var tiePrivatePoints = new ArrayList<Player>();
        Player bestScoringPlayer = null;
        var highestScore = 0;
        for (var player : tiePlayerList) {
            var points = player.getPrivateObjectiveCard().calculatePoints(player.getPlayerFrame());

            if (bestScoringPlayer == null) {
                bestScoringPlayer = player;
                highestScore = points;
            } else {
                if (highestScore == points) {
                    if (!tiePrivatePoints.contains(bestScoringPlayer)) {
                        tiePrivatePoints.add(bestScoringPlayer);
                    }
                    tiePrivatePoints.add(player);
                } else if (highestScore < points) {
                    bestScoringPlayer = player;
                    highestScore = points;
                }
            }
        }

        if (tiePrivatePoints.isEmpty()) {
            return bestScoringPlayer;
        }

        var tieFavorPoints = new ArrayList<Player>();
        bestScoringPlayer = null;
        highestScore = 0;
        for (var player : tiePlayerList) {
            var points = player.getFavorTokens().size();

            if (bestScoringPlayer == null) {
                bestScoringPlayer = player;
                highestScore = points;
            } else {
                if (highestScore == points) {
                    if (!tieFavorPoints.contains(bestScoringPlayer)) {
                        tieFavorPoints.add(bestScoringPlayer);
                    }
                    tieFavorPoints.add(player);
                } else if (highestScore < points) {
                    bestScoringPlayer = player;
                    highestScore = points;
                }
            }
        }

        if (tieFavorPoints.isEmpty()) {
            return bestScoringPlayer;
        }

        // TODO: decide winner by reverse player order
    }
}
