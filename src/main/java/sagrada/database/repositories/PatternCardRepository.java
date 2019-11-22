package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.PatternCard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

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
    public void update(PatternCard patternCard) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "UPDATE patterncard SET `name` = ?, `difficulty` = ?, `standard` = ? WHERE `idpatterncard` = ?;"
        );

        preparedStatement.setString(1, patternCard.getName());
        preparedStatement.setInt(2, patternCard.getDifficulty());
        preparedStatement.setBoolean(3, patternCard.getStandard());

        preparedStatement.setInt(4, patternCard.getId());

        preparedStatement.executeUpdate();
    }

    @Override
    public void updateMultiple(Collection<PatternCard> patternCards) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "UPDATE patterncard SET `name` = ?, `difficulty` = ?, `standard` = ? WHERE `idpatterncard` = ?;"
        );

        int count = 0;

        for (PatternCard patternCard : patternCards) {
            preparedStatement.setString(1, patternCard.getName());
            preparedStatement.setInt(2, patternCard.getDifficulty());
            preparedStatement.setBoolean(3, patternCard.getStandard());

            preparedStatement.setInt(4, patternCard.getId());

            preparedStatement.addBatch();

            count++;

            if (count % BATCH_SIZE == 0 || count == patternCards.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    @Override
    public void delete(PatternCard patternCard) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("DELETE FROM patterncard WHERE idpatterncard = ?;");

        preparedStatement.setInt(1, patternCard.getId());

        preparedStatement.executeUpdate();
    }

    @Override
    public void deleteMultiple(Collection<PatternCard> patternCards) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "DELETE FROM patterncard WHERE idpatterncard = ?;"
        );

        int count = 0;

        for (PatternCard patternCard : patternCards) {
            preparedStatement.setInt(1, patternCard.getId());

            preparedStatement.addBatch();

            count++;

            if (count % BATCH_SIZE == 0 || count == patternCards.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    @Override
    public void add(PatternCard patternCard) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("INSERT INTO patterncard values(?, ?, ?);");

        preparedStatement.setString(1, patternCard.getName());
        preparedStatement.setInt(2, patternCard.getDifficulty());
        preparedStatement.setBoolean(3, patternCard.getStandard());

        preparedStatement.executeUpdate();
    }

    @Override
    public void addMultiple(Collection<PatternCard> patternCards) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "INSERT INTO patterncard values(?, ?, ?);"
        );

        int count = 0;

        for (PatternCard patternCard : patternCards) {
            preparedStatement.setString(1, patternCard.getName());
            preparedStatement.setInt(2, patternCard.getDifficulty());
            preparedStatement.setBoolean(3, patternCard.getStandard());

            preparedStatement.addBatch();

            count++;

            if (count % BATCH_SIZE == 0 || count == patternCards.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }
}
