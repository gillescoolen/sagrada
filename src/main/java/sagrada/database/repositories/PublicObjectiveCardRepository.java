package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.ObjectiveCard;
import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;
import sagrada.model.card.CardFactory;
import sagrada.model.card.objective.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public final class PublicObjectiveCardRepository extends Repository<PublicObjectiveCard> {
    public PublicObjectiveCardRepository(DatabaseConnection connection) {
        super(connection);
    }

    public PublicObjectiveCard findByName(String name) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM public_objectivecard WHERE `name` = ?");
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.getFetchSize() > 1) {
            throw new SQLException("Multiple results, expected 1.");
        }

        if (!resultSet.next()) {
            return null;
        }

        final int id = resultSet.getInt("id");
        final String description = resultSet.getString("description");
        final int points = resultSet.getInt("points");

        resultSet.close();
        preparedStatement.close();

        return CardFactory.getPublicObjectiveCard(name, id, description, points);
    }

    @Override
    public PublicObjectiveCard findById(int id) throws SQLException {
        return null;
    }

    @Override
    public void update(PublicObjectiveCard model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<PublicObjectiveCard> models) throws SQLException {

    }

    @Override
    public void delete(PublicObjectiveCard model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<PublicObjectiveCard> models) throws SQLException {

    }

    @Override
    public void add(PublicObjectiveCard model) throws SQLException {

    }

    @Override
    public void addMultiple(Collection<PublicObjectiveCard> models) throws SQLException {

    }
}
