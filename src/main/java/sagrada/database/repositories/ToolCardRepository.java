package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.ToolCard;
import sagrada.model.card.CardFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ToolCardRepository extends Repository<ToolCard> {
    public ToolCardRepository(DatabaseConnection connection) {
        super(connection);
    }

    public List<ToolCard> getRandom() throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM toolcard ORDER BY RAND() LIMIT 3;");

        ResultSet resultSet = preparedStatement.executeQuery();
        List<ToolCard> toolCards = new ArrayList<>();

        while (resultSet.next()) {
            toolCards.add(CardFactory.getToolCard(
                    resultSet.getInt("idtoolcard"),
                    resultSet.getString("name"),
                    resultSet.getString("description")
            ));
        }

        return toolCards;
    }

    public ToolCard findByName(String name) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM toolcard WHERE `name` = ?");
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return null;
        }

        final int id = resultSet.getInt("idtoolcard");
        final String description = resultSet.getString("description");

        preparedStatement.close();
        resultSet.close();

        return CardFactory.getToolCard(id, name, description);
    }

    @Override
    public ToolCard findById(int id) throws SQLException {
        return null;
    }

    @Override
    public void update(ToolCard model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<ToolCard> models) throws SQLException {

    }

    @Override
    public void delete(ToolCard model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<ToolCard> models) throws SQLException {

    }

    @Override
    public void add(ToolCard model) throws SQLException {

    }

    @Override
    public void addMultiple(Collection<ToolCard> models) throws SQLException {

    }
}
