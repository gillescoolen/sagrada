package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.Color;
import sagrada.model.Die;
import sagrada.model.RoundTrack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public final class RoundTrackRepository extends Repository<RoundTrack> {
    public RoundTrackRepository(DatabaseConnection connection) {
        super(connection);
    }

    @Override
    public RoundTrack findById(int id) throws SQLException {
        return null;
    }

    @Override
    public void update(RoundTrack model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<RoundTrack> models) throws SQLException {

    }

    @Override
    public void delete(RoundTrack model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<RoundTrack> models) throws SQLException {

    }

    @Override
    public void add(RoundTrack model) throws SQLException {

    }

    @Override
    public void addMultiple(Collection<RoundTrack> models) throws SQLException {

    }

    public void addRoundToRoundTrack(List<Die> dice, int round, int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "UPDATE gamedie SET roundtrack = ? WHERE idgame = ? AND dienumber = ? AND diecolor = ? AND value = ?;"
        );

        var count = 0;

        for (var die : dice) {
            preparedStatement.setInt(1, round);
            preparedStatement.setInt(2, gameId);
            preparedStatement.setInt(3, die.getNumber());
            preparedStatement.setString(4, die.getColor().getDutchColorName());
            preparedStatement.setInt(5, die.getValue());

            preparedStatement.addBatch();

            ++count;

            if (count % BATCH_SIZE == 0 || count == dice.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    public RoundTrack getRoundTrack(int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "SELECT * FROM gamedie WHERE idgame = ? AND roundtrack IS NOT NULL;"
        );
        preparedStatement.setInt(1, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();

        RoundTrack roundTrack = new RoundTrack();

        while (resultSet.next()) {
            var trackRound = resultSet.getInt("roundTrack");
            var dieNumber = resultSet.getInt("dienumber");
            var dieColor = resultSet.getString("diecolor");
            var dieValue = resultSet.getInt("value");

            Die die = new Die(dieNumber, Color.fromString(dieColor));
            die.setValue(dieValue);

            roundTrack.putTrack(trackRound, die);
        }

        roundTrack.setCurrent(this.getCurrentRound(gameId));

        preparedStatement.close();
        resultSet.close();

        return roundTrack;
    }

    public int getCurrentRound(int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "SELECT roundtrack FROM gamedie WHERE idgame = ? ORDER BY roundtrack DESC LIMIT 1;"
        );
        preparedStatement.setInt(1, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();

        int currentRound = 0;

        while (resultSet.next()) {
            currentRound = resultSet.getInt("roundtrack");

            if (currentRound == 0) {
                currentRound = 1;
            }
        }

        preparedStatement.close();
        resultSet.close();

        return currentRound;
    }
}

