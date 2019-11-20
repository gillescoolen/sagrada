package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.PatternCard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class PatternCardRepository extends Repository<PatternCard> {

    PatternCardRepository(DatabaseConnection connection) {
        super(connection);
    }

    @Override
    public PatternCard findById(int id) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM patterncard WHERE idpatterncard = ?;");

        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.getFetchSize() > 1) {
            throw new SQLException("Multiple results, expected 1.");
        }

        final int idPatternCard = resultSet.getInt("idpatterncard");
        final String name = resultSet.getString("name");
        final int difficulty = resultSet.getInt("difficulty");
        final boolean standard = resultSet.getBoolean("standard");

        resultSet.close();
        preparedStatement.close();

        return new PatternCard(idPatternCard, name, difficulty, standard);
    }

    @Override
    public void update(PatternCard model) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "UPDATE patterncard SET `name` = ?, `difficulty` = ?, `standard` = ? WHERE `idpatterncard` = ?;"
        );

        preparedStatement.setString(1, model.getName());
        preparedStatement.setInt(2, model.getDifficulty());
        preparedStatement.setBoolean(3, model.getStandard());

        preparedStatement.setInt(4, model.getId());

        int rowsUpdated = preparedStatement.executeUpdate();

        assert (rowsUpdated == 1) : "multiple rows were updated";
    }

    @Override
    public void updateMultiple(Iterable<PatternCard> models) throws SQLException {

    }

    @Override
    public void delete(PatternCard model) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("DELETE FROM patterncard WHERE idpatterncard = ?;");

        preparedStatement.setInt(1, model.getId());

        int rowsDeleted = preparedStatement.executeUpdate();

        assert (rowsDeleted == 1) : "multiple rows were deleted";
    }

    @Override
    public void deleteMultiple(Iterable<PatternCard> models) throws SQLException {

    }

    @Override
    public void add(PatternCard model) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("INSERT INTO patterncard values(?, ?, ?);");

        preparedStatement.setString(1, model.getName());
        preparedStatement.setInt(2, model.getDifficulty());
        preparedStatement.setBoolean(3, model.getStandard());

        int rowsAdded = preparedStatement.executeUpdate();

        assert (rowsAdded == 1) : "multiple rows were added";
    }

    @Override
    public void addMultiple(Iterable<PatternCard> models) throws SQLException {

    }
}
