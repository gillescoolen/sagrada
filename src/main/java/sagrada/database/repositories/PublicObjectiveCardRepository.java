package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.PublicObjectiveCard;
import sagrada.model.card.CardFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PublicObjectiveCardRepository extends Repository<PublicObjectiveCard> {
    public PublicObjectiveCardRepository(DatabaseConnection connection) {
        super(connection);
    }

    public List<PublicObjectiveCard> getRandom() throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM public_objectivecard ORDER BY RAND() LIMIT 3;");

        ResultSet resultSet = preparedStatement.executeQuery();
        List<PublicObjectiveCard> publicObjectiveCards = new ArrayList<>();

        while (resultSet.next()) {
            publicObjectiveCards.add(CardFactory.getPublicObjectiveCard(
                    resultSet.getString("name"),
                    resultSet.getInt("idpublic_objectivecard"),
                    resultSet.getString("description"),
                    resultSet.getInt("points")
            ));
        }

        return publicObjectiveCards;
    }

    public PublicObjectiveCard findByName(String name) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM public_objectivecard WHERE `name` = ?");
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();

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
