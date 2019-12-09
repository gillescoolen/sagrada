package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;
import sagrada.util.ChatLinePair;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public final class ChatRepository extends Repository<ChatLine> {
    public ChatRepository(DatabaseConnection connection) {
        super(connection);
    }


    public List<ChatLinePair> getMultiple(LocalDateTime time, Integer id) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT username, message FROM game JOIN player p on game.idgame = p.spel_idspel JOIN chatline c on p.idplayer = c.player_idplayer WHERE idgame = ? AND time > ?");

        preparedStatement.setInt(1, id);
        preparedStatement.setTimestamp(2, Timestamp.valueOf(time));

        ResultSet resultSet = preparedStatement.executeQuery();

        List<ChatLinePair> lines = new ArrayList<ChatLinePair>();

        while (resultSet.next()) {
            lines.add(new ChatLinePair(resultSet.getString("username"), resultSet.getString("message")));
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
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("INSERT INTO chatline (player_idplayer, time, message) VALUES (?, ?, ?)");

        preparedStatement.setInt(1, model.getPlayer().getId());
        preparedStatement.setTimestamp(2, Timestamp.valueOf(model.getTimestamp()));
        preparedStatement.setString(3, model.getMessage());

        preparedStatement.execute();
        preparedStatement.close();
    }

    @Override
    public void addMultiple(Collection<ChatLine> models) throws SQLException {

    }
}
