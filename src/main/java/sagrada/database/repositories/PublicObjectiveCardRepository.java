package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.PatternCard;
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

        preparedStatement.close();
        resultSet.close();

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

    public List<PublicObjectiveCard> getAllByGameId(int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM gamepublic_objectivecard WHERE idspel = ?");

        preparedStatement.setInt(1, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();
        List<PublicObjectiveCard> publicObjectiveCards = new ArrayList<>();

        while (resultSet.next()) {
            PreparedStatement cardPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM public_objectivecard WHERE idpublic_objectivecard = ?");

            cardPreparedStatement.setInt(1, resultSet.getInt("iddoelkaart_gedeeld"));

            ResultSet cardResultSet = cardPreparedStatement.executeQuery();

            if (!cardResultSet.next()) {
                break;
            }

            publicObjectiveCards.add(CardFactory.getPublicObjectiveCard(
                    cardResultSet.getString("name"),
                    cardResultSet.getInt("idpublic_objectivecard"),
                    cardResultSet.getString("description"),
                    cardResultSet.getInt("points")
            ));

            cardPreparedStatement.close();
            cardResultSet.close();
        }

        preparedStatement.close();
        resultSet.close();

        return publicObjectiveCards;
    }

    public void addMultiple(Collection<PublicObjectiveCard> publicObjectiveCards, int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "INSERT INTO gamepublic_objectivecard (idspel, iddoelkaart_gedeeld) VALUES (?, ?);"
        );

        var count = 0;

        for (var publicObjectiveCard : publicObjectiveCards) {
            preparedStatement.setInt(1, gameId);
            preparedStatement.setInt(2, publicObjectiveCard.getId());

            preparedStatement.addBatch();

            ++count;

            if (count % BATCH_SIZE == 0 || count == publicObjectiveCards.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }
}
