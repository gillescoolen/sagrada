package sagrada.database.repositories;

import javafx.util.Pair;
import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public final class GameRepository extends Repository<Game> {
    public GameRepository(DatabaseConnection connection) {
        super(connection);
    }

    public void setCurrentPlayer(int playerId, int gameId) throws SQLException {
        this.startGame(playerId, gameId);
    }

    public void startGame(int turnPlayerId, int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("UPDATE game SET turn_idplayer = ? WHERE idgame = ?");
        preparedStatement.setInt(1, turnPlayerId);
        preparedStatement.setInt(2, gameId);
        preparedStatement.execute();
        preparedStatement.close();
    }

    public int countAllGames() throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT COUNT(*) AS allGames FROM game;");
        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return 0;
        }

        return resultSet.getInt("allGames");
    }

    public List<Game> getAll(int offset, boolean orderDesc) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(orderDesc ? "SELECT * FROM game g ORDER BY g.created_on DESC LIMIT ?, 20;" : "SELECT * FROM game g ORDER BY g.created_on LIMIT ?, 20;");

        preparedStatement.setInt(1, offset);

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Game> games = new ArrayList<>();

        while (resultSet.next()) {
            Game game = this.getGame(resultSet);

            if (game.getPlayers().size() > 0) {
                games.add(game);
            }
        }

        resultSet.close();
        preparedStatement.close();

        return games;
    }

    public List<Game> getInvitedGames(Account account) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player p JOIN game g ON p.spel_idspel = g.idgame WHERE p.username = ? AND p.playstatus_playstatus = ? ORDER BY g.created_on DESC;");

        preparedStatement.setString(1, account.getUsername());
        preparedStatement.setString(2, PlayStatus.INVITED.getPlayState());

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Game> invitedGames = new ArrayList<>();

        while (resultSet.next()) {
            invitedGames.add(this.getGame(resultSet));
        }

        preparedStatement.close();
        resultSet.close();

        return invitedGames;
    }

    private Game getGame(ResultSet resultSet) throws SQLException {
        var game = new Game();

        game.setId(resultSet.getInt("idgame"));
        game.setCreatedOn(resultSet.getTimestamp("created_on").toLocalDateTime());

        PreparedStatement playerPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player p WHERE spel_idspel = ? AND p.playstatus_playstatus IN (?, ?, ?, ?);");
        playerPreparedStatement.setInt(1, resultSet.getInt("idgame"));
        playerPreparedStatement.setString(2, PlayStatus.ACCEPTED.getPlayState());
        playerPreparedStatement.setString(3, PlayStatus.CHALLENGER.getPlayState());
        playerPreparedStatement.setString(4, PlayStatus.INVITED.getPlayState());
        playerPreparedStatement.setString(5, PlayStatus.DONE_PLAYING.getPlayState());

        ResultSet playerResultSet = playerPreparedStatement.executeQuery();

        while (playerResultSet.next()) {
            PlayStatus playerPlayStatus = PlayStatus.DONE_PLAYING;
            var playerAccount = new Account(playerResultSet.getString("username"));

            for (PlayStatus playStatus : PlayStatus.values()) {
                if (playStatus.getPlayState().equals(playerResultSet.getString("playstatus_playstatus"))) {
                    playerPlayStatus = playStatus;
                }
            }

            var player = new Player();

            player.setId(playerResultSet.getInt("idplayer"));
            player.setPlayStatus(playerPlayStatus);
            player.setCurrentPlayer(playerResultSet.getInt("isCurrentPlayer") > 0);
            player.setScore(playerResultSet.getInt("score"));
            player.setAccount(playerAccount);

            game.addPlayer(player);
        }

        playerResultSet.close();
        playerPreparedStatement.close();

        return game;
    }

    public Integer getLatestGameId() throws SQLException {
        Integer id = null;

        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM game WHERE idgame = (SELECT MAX(idgame) FROM game);");

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            id = resultSet.getInt("idgame");
        }

        resultSet.close();
        preparedStatement.close();

        return id;
    }

    public int getCurrentRound(int gameId) throws SQLException {
        int round = 0;

        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT COALESCE(MAX(round), 1) AS round FROM gamedie WHERE idgame = ?;");
        preparedStatement.setInt(1, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            round = resultSet.getInt("round");
        }

        resultSet.close();
        preparedStatement.close();

        return round;
    }

    public int getNextRound(int gameId) throws SQLException {
        int round = 0;

        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT COALESCE(MAX(round), 0) + 1 AS round FROM gamedie WHERE idgame = ?;");
        preparedStatement.setInt(1, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            round = resultSet.getInt("round");
        }

        resultSet.close();
        preparedStatement.close();

        return round;
    }

    @Override
    public Game findById(int id) throws SQLException {
        Game game = new Game();

        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM game WHERE idgame = ?");
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            game = this.getGame(resultSet);
        }

        resultSet.close();
        preparedStatement.close();

        return game;
    }

    public boolean checkIfGameHasStarted(Game game) throws SQLException {
        boolean started = false;

        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM game WHERE idgame = ? AND turn_idplayer IS NOT NULL;");
        preparedStatement.setInt(1, game.getId());

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            started = true;
        }

        preparedStatement.close();
        resultSet.close();

        return started;
    }

    @Override
    public void update(Game model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<Game> models) throws SQLException {

    }

    @Override
    public void delete(Game model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<Game> models) throws SQLException {

    }

    @Override
    public void add(Game model) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("INSERT INTO game (created_on) VALUES (?)");

        preparedStatement.setTimestamp(1, Timestamp.valueOf(model.getCreatedOn()));

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    @Override
    public void addMultiple(Collection<Game> models) throws SQLException {

    }

    public void updateGamePlayer(int nextPlayerId, Game game) throws SQLException {
        PreparedStatement nextPlayerGameStatement = this.connection.getConnection().prepareStatement("UPDATE game SET turn_idplayer = ? WHERE idgame = ?");

        nextPlayerGameStatement.setInt(1, nextPlayerId);
        nextPlayerGameStatement.setInt(2, game.getId());

        nextPlayerGameStatement.executeUpdate();

        nextPlayerGameStatement.close();
    }

    public List<Player> getAllDonePlayers(Game game) throws SQLException {
        var players = new ArrayList<Player>();

        PreparedStatement playerPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player WHERE spel_idspel = ? AND playstatus_playstatus IN (?)");
        playerPreparedStatement.setInt(1, game.getId());
        playerPreparedStatement.setString(2, PlayStatus.DONE_PLAYING.getPlayState());
        ResultSet playerResultSet = playerPreparedStatement.executeQuery();

        PlayerRepository playerRepository = new PlayerRepository(this.connection);

        while (playerResultSet.next()) {
            players.add(playerRepository.createPlayer(playerResultSet));
        }

        playerPreparedStatement.close();
        playerResultSet.close();

        return players;
    }
}

