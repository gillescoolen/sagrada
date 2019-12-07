package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.Color;
import sagrada.model.Die;

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
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT d.number, d.color, g.idgame FROM die d LEFT OUTER JOIN gamedie g ON g.dienumber = d.number AND g.diecolor = d.color AND g.idgame = ? WHERE g.idgame IS NULL;");

        preparedStatement.setInt(1, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Die> unusedDice = new ArrayList<>();

        while (resultSet.next()) {
            Die die = null;
            for (Color color : Color.values()) {
                if (color.getDutchColorName().equals(resultSet.getString("color"))) {
                    die = new Die(color);
                }
            }
            unusedDice.add(die);
        }

        return unusedDice;
    }

    public List<Die> getDraftPoolDice(int gameId, int round) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT d.number, d.color FROM die d LEFT OUTER JOIN gamedie g ON g.dienumber = d.number AND g.diecolor = d.color AND g.idgame = ? LEFT JOIN playerframefield p ON p.dienumber = d.number AND p.diecolor = d.color AND g.idgame = ? WHERE g.idgame IS NOT NULL AND p.idgame IS NULL AND g.roundtrack IS NULL AND g.round = ?;");

        preparedStatement.setInt(1, gameId);
        preparedStatement.setInt(2, gameId);
        preparedStatement.setInt(3, round);

        ResultSet resultSet = preparedStatement.executeQuery();
        List<Die> draftPoolDice = new ArrayList<>();

        while (resultSet.next()) {
            Die die = null;
            for (Color color : Color.values()) {
                if (color.getDutchColorName().equals(resultSet.getString("color"))) {
                    die = new Die(color);
                }
            }
            draftPoolDice.add(die);
        }

        return draftPoolDice;
    }

    @Override
    public Die findById(int id) throws SQLException {
        throw new SQLException("Die has no id");
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
