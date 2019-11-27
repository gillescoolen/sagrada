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

            PreparedStatement playerPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player p WHERE spel_idspel = ?;");
            playerPreparedStatement.setInt(1, resultSet.getInt("idgame"));
            ResultSet playerResultSet = playerPreparedStatement.executeQuery();

            while (playerResultSet.next()) {
                PlayStatus playerPlayStatus = PlayStatus.DONE_PLAYING;
                Color playerCardColor = Color.RED;
                var playerAccount = new Account(playerResultSet.getString("username"));

                for (PlayStatus playStatus : PlayStatus.values()) {
                    if (playStatus.getPlayState().equals(playerResultSet.getString("playstatus_playstatus"))) {
                        playerPlayStatus = playStatus;
                    }
                }

                for (Color color : Color.values()) {
                    if (color.getColor().equals(playerResultSet.getString("private_objectivecard_color"))) {
                        playerCardColor = color;
                    }
                }

                var player = new Player();

                player.setId(playerResultSet.getInt("idplayer"));
                player.setAccount(playerAccount);
                player.setPlayStatus(playerPlayStatus);
                player.setCurrentPlayer(playerResultSet.getInt("isCurrentPlayer") > 0);
                player.setPrivateObjectiveCard(new PrivateObjectiveCard(playerCardColor));

                game.addPlayer(player);
            }

            games.add(game);
        }

        return games;
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
