package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameRepository extends Repository<Game> {
    public GameRepository(DatabaseConnection connection) {
        super(connection);
    }

    public List<Game> getAll() throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM game g ORDER BY g.created_on DESC LIMIT 20;");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Game> games = new ArrayList<>();

        while (resultSet.next()) {
            var game = new Game();
            var timeStamp = resultSet.getTimestamp("created_on");

            game.setCreatedOn(timeStamp.toLocalDateTime());
            game.setId(resultSet.getInt("idgame"));

            PreparedStatement playerPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player p WHERE spel_idspel = ? AND p.playstatus_playstatus IN (?, ?);");
            playerPreparedStatement.setInt(1, resultSet.getInt("idgame"));
            playerPreparedStatement.setString(2, PlayStatus.ACCEPTED.getPlayState());
            playerPreparedStatement.setString(3, PlayStatus.CHALLENGER.getPlayState());
            ResultSet playerResultSet = playerPreparedStatement.executeQuery();

            while (playerResultSet.next()) {
                PlayStatus playerPlayStatus = PlayStatus.DONE_PLAYING;
                var account = new Account(playerResultSet.getString("username"));

                for (PlayStatus playStatus : PlayStatus.values()) {
                    if (playStatus.getPlayState().equals(playerResultSet.getString("playstatus_playstatus"))) {
                        playerPlayStatus = playStatus;
                    }
                }

                var player = new Player();

                player.setId(playerResultSet.getInt("idplayer"));
                player.setPlayStatus(playerPlayStatus);
                player.setCurrentPlayer(playerResultSet.getInt("isCurrentPlayer") > 0);
                player.setAccount(account);

                game.addPlayer(player);
            }

            if (game.getPlayers().size() > 0) {
                games.add(game);
            }
        }

        return games;
    }

    public List<Game> getInvitedGames(Account account) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player p JOIN game g ON p.spel_idspel = g.idgame WHERE p.username = ? AND p.playstatus_playstatus = ? ORDER BY g.created_on DESC LIMIT 20;");

        preparedStatement.setString(1, account.getUsername());
        preparedStatement.setString(2, PlayStatus.INVITED.getPlayState());

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Game> invitedGames = new ArrayList<>();

        while (resultSet.next()) {
            var game = new Game();

            game.setId(resultSet.getInt("idgame"));
            game.setCreatedOn(resultSet.getTimestamp("created_on").toLocalDateTime());

            PreparedStatement playerPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player p WHERE spel_idspel = ? AND p.playstatus_playstatus IN (?, ?);");
            playerPreparedStatement.setInt(1, resultSet.getInt("idgame"));
            playerPreparedStatement.setString(2, PlayStatus.ACCEPTED.getPlayState());
            playerPreparedStatement.setString(3, PlayStatus.CHALLENGER.getPlayState());
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
                player.setAccount(playerAccount);

                game.addPlayer(player);
            }

            invitedGames.add(game);
        }

        return invitedGames;
    }

    @Override
    public Game findById(int id) throws SQLException {
        return null;
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

    }

    @Override
    public void addMultiple(Collection<Game> models) throws SQLException {

    }
}
