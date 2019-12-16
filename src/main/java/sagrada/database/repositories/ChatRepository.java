package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public final class ChatRepository extends Repository<ChatLine> {
    public ChatRepository(DatabaseConnection connection) {
        super(connection);
    }


    public List<String> getMultiple(Integer id) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT username, message, time FROM game JOIN player p on game.idgame = p.spel_idspel JOIN chatline c on p.idplayer = c.player_idplayer WHERE idgame = ? ORDER BY c.time DESC;");

        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        List<String> lines = new ArrayList<>();

        while (resultSet.next()) {
            lines.add( resultSet.getTimestamp("time") + " [" + resultSet.getString("username") + "] - " + resultSet.getString("message"));
        }

        resultSet.close();
        preparedStatement.close();

        return lines;
    }

    @Override
    public ChatLine findById(int id) throws SQLException {
        return null;
    }

    @Override
    public void update(ChatLine model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<ChatLine> models) throws SQLException {

    }

    @Override
    public void delete(ChatLine model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<ChatLine> models) throws SQLException {

    }

    @Override
    public void add(ChatLine model) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("INSERT INTO chatline (player_idplayer, time, message) VALUES (?, NOW(), ?)");

        preparedStatement.setInt(1, model.getPlayer().getId());
        preparedStatement.setString(2, model.getMessage());

        preparedStatement.execute();
        preparedStatement.close();
    }

    @Override
    public void addMultiple(Collection<ChatLine> models) throws SQLException {

    }
}
