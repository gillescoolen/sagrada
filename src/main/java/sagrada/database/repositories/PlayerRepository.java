package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.Game;
import sagrada.model.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class PlayerRepository extends Repository<Player> {
    public PlayerRepository(DatabaseConnection connection) {
        super(connection);
    }

    @Override
    public Player findById(int id) throws SQLException {


        return null;
    }

    @Override
    public void update(Player model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<Player> models) throws SQLException {

    }

    @Override
    public void delete(Player model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<Player> models) throws SQLException {

    }

    public void add(Player player, Game game) throws SQLException {

        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("INSERT INTO player (username, spel_idspel, playstatus_playstatus, isCurrentPlayer, private_objectivecard_color) " +
                        "VALUES(?,?,?,?,?)");

        preparedStatement.setString(1, player.getAccount().getUsername());
        preparedStatement.setInt(2, game.getId());
        preparedStatement.setString(3, player.getPlayStatus().getPlayState());
        preparedStatement.setByte(4, ((byte)0));
        preparedStatement.setString(5, player.getPrivateObjectiveCard().getColor().getColor());

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    @Override
    public void add(Player model) throws SQLException {

    }

    @Override
    public void addMultiple(Collection<Player> models) throws SQLException {

    }
}
