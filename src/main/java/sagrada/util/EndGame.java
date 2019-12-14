package sagrada.util;

import sagrada.database.DatabaseConnection;
import sagrada.model.Game;
import sagrada.model.Player;

import java.util.ArrayList;

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

        Player bestScoringPlayer = null;
        for (var player : this.game.getPlayers()) {
            var points = 0;

            player.setPlayerFrame(player.validateFrameField(this.connection, this.game));

            for (var objectiveCard : this.game.getObjectiveCards()) {
                points += objectiveCard.calculatePoints(player.getPlayerFrame());
            }

            points += player.getPrivateObjectiveCard().calculatePoints(player.getPlayerFrame());
            points += player.getFavorTokens().size();
            points -= player.getPlayerFrame().countEmptySquares();

            player.setScore(points);

            if (bestScoringPlayer == null) {
                bestScoringPlayer = player;
            } else {
                if (bestScoringPlayer.getScore() == player.getScore()) {
                    if (!tiePlayerList.contains(bestScoringPlayer)) {
                        tiePlayerList.add(bestScoringPlayer);
                    }
                    tiePlayerList.add(player);
                } else if (bestScoringPlayer.getScore() < player.getScore()) {
                    tiePlayerList.clear();
                    bestScoringPlayer = player;
                }
            }

            updatedPlayerList.add(player);
        }

        if (!tiePlayerList.isEmpty()) {
            var winner = getWinnerWhenTie(tiePlayerList);
            winner.setScore(winner.getScore() + 1);
        }

        this.game.addPlayers(updatedPlayerList);
    }

    private Player getWinnerWhenTie(ArrayList<Player> tiePlayerList) {
        Player winner = this.winningPlayer(tiePlayerList, "poc_points");
        if (winner != null) {
            return winner;
        }

        winner = this.winningPlayer(tiePlayerList, "ft_points");
        if (winner != null) {
            return winner;
        }

        for (var player : tiePlayerList) {
            if (winner == null || winner.getSequenceNumber() < player.getSequenceNumber()) {
                winner = player;
            }
        }

        return winner;
    }

    private Player winningPlayer(ArrayList<Player> playerList, String type) {
        var tiePlayerList = new ArrayList<Player>();

        Player bestScoringPlayer = null;
        var highestScore = 0;
        for (var player : playerList) {
            var points = 0;
            if (type.equals("poc_points")) {
                points = player.getPrivateObjectiveCard().calculatePoints(player.getPlayerFrame());
            } else if (type.equals("ft_points")) {
                points = player.getFavorTokens().size();
            }

            if (bestScoringPlayer == null) {
                bestScoringPlayer = player;
                highestScore = points;
            } else {
                if (highestScore == points) {
                    if (!tiePlayerList.contains(bestScoringPlayer)) {
                        tiePlayerList.add(bestScoringPlayer);
                    }
                    tiePlayerList.add(player);
                } else if (highestScore < points) {
                    bestScoringPlayer = player;
                    highestScore = points;
                }
            }
        }

        if (tiePlayerList.isEmpty()) {
            return bestScoringPlayer;
        }

        return null;
    }
}
