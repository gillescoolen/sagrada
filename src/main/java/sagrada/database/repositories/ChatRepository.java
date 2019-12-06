package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;

public class ChatRepository extends Repository<ChatLine> {
    public ChatRepository(DatabaseConnection connection) {
        super(connection);
    }


    public void getMultiple(Date date) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("");

        ResultSet resultSet = preparedStatement.executeQuery();
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
    }

    @Override
    public void addMultiple(Collection<ChatLine> models) throws SQLException {

    }

}
