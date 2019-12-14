package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.Color;
import sagrada.model.Die;
import sagrada.model.PatternCard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DieRepository extends Repository<Die> {

    public DieRepository(DatabaseConnection connection) {
        super(connection);
    }

    public List<Die> getUnusedDice(int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT d.number, d.color, g.value FROM die d LEFT OUTER JOIN gamedie g ON g.dienumber = d.number AND g.diecolor = d.color AND g.idgame = ? WHERE g.idgame IS NULL;");

        preparedStatement.setInt(1, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Die> unusedDice = new ArrayList<>();

        while (resultSet.next()) {
            Die die = null;
            for (Color color : Color.values()) {
                if (color.getDutchColorName().equals(resultSet.getString("color"))) {
                    die = new Die(resultSet.getInt("number"), color);
                    die.setValue(resultSet.getInt("value"));
                }
            }
            unusedDice.add(die);
        }

        preparedStatement.close();
        resultSet.close();

        return unusedDice;
    }

    public List<Die> getDraftPoolDice(int gameId, int round) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT g.diecolor, g.dienumber, g.value FROM playerframefield p RIGHT OUTER JOIN gamedie g ON g.idgame = p.idgame AND g.dienumber = p.dienumber AND g.diecolor = p.diecolor WHERE g.idgame = ? AND g.round = ? AND p.dienumber IS NULL AND p.diecolor IS NUll;");

        preparedStatement.setInt(1, gameId);
        preparedStatement.setInt(2, round);

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Die> draftPoolDice = new ArrayList<>();

        while (resultSet.next()) {
            Die die = null;
            for (Color color : Color.values()) {
                if (color.getDutchColorName().equals(resultSet.getString("diecolor"))) {
                    die = new Die(resultSet.getInt("dienumber"), color);
                    die.setValue(resultSet.getInt("value"));
                }
            }
            draftPoolDice.add(die);
        }

        preparedStatement.close();
        resultSet.close();

        return draftPoolDice;
    }

    public void addGameDice(int gameId, int round, Collection<Die> dice) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("INSERT INTO gamedie (idgame, dienumber, diecolor, round, value) VALUES (?, ?, ?, ?, ?);");

        int count = 0;

        for (Die die : dice) {
            preparedStatement.setInt(1, gameId);
            preparedStatement.setInt(2, die.getNumber());
            preparedStatement.setString(3, die.getColor().getDutchColorName());
            preparedStatement.setInt(4, round);
            preparedStatement.setInt(5, die.getValue());

            preparedStatement.addBatch();

            count++;

            if (count % BATCH_SIZE == 0 || count == dice.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    @Override
    public Die findById(int id) throws SQLException {
        throw new SQLException("Die has no id");
    }

    public Die findById(int idGame, int dieNumber, String dieColor) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM gamedie WHERE idgame = ? AND dienumber = ? AND diecolor = ?");

        preparedStatement.setInt(1, idGame);
        preparedStatement.setInt(2, dieNumber);
        preparedStatement.setString(3, dieColor);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return null;
        }

        int foundDieNumber = resultSet.getInt("dienumber");
        Color foundDieColor = null;

        for (Color color : Color.values()) {
            if (color.getDutchColorName().equals(resultSet.getString("diecolor"))) {
                foundDieColor = color;
            }
        }

        int value = resultSet.getInt("value");

        Die die = new Die(foundDieNumber, foundDieColor);
        die.setValue(value);

        preparedStatement.close();
        resultSet.close();

        return die;
    }

    public boolean placedDieThisRound(int gameId, int playerId, int round) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM playerframefield p INNER JOIN gamedie g ON p.idgame = g.idgame AND p.dienumber = g.dienumber AND p.diecolor = g.diecolor WHERE p.idgame = ? AND p.player_idplayer = ? AND g.round = ?");
        preparedStatement.setInt(1, gameId);
        preparedStatement.setInt(2, playerId);
        preparedStatement.setInt(3, round);

        ResultSet resultSet = preparedStatement.executeQuery();

        boolean placed = false;

        if (resultSet.next()) {
            placed = true;
        }

        preparedStatement.close();
        resultSet.close();

        return placed;
    }

    @Override
    public void update(Die model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<Die> models) throws SQLException {

    }

    @Override
    public void delete(Die model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<Die> models) throws SQLException {

    }

    @Override
    public void add(Die model) throws SQLException {
    }

    @Override
    public void addMultiple(Collection<Die> models) throws SQLException {

    }
}
