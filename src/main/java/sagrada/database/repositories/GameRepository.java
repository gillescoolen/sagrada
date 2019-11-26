package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.Game;

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
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM game;");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Game> games = new ArrayList<>();

        while (resultSet.next()) {
            var game = new Game();
            var timeStamp = resultSet.getTimestamp("created_on");

            game.setCreatedOn(timeStamp.toLocalDateTime());
            game.setId(resultSet.getInt("idgame"));

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
