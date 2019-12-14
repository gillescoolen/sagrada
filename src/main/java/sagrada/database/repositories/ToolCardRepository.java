package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.Die;
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
                    resultSet.getString("description"),
                    this.connection
            ));
        }

        preparedStatement.close();
        resultSet.close();

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

        return CardFactory.getToolCard(id, name, description, this.connection);
    }

    @Override
    public ToolCard findById(int id) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("SELECT * FROM toolcard WHERE idtoolcard = ?");
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return null;
        }

        final int idtoolcard = resultSet.getInt("idtoolcard");
        final String name = resultSet.getString("name");
        final String description = resultSet.getString("description");

        preparedStatement.close();
        resultSet.close();

        return CardFactory.getToolCard(idtoolcard, name, description, this.connection);
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

    public List<ToolCard> getAllByGameId(int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM gametoolcard WHERE idgame = ?");

        preparedStatement.setInt(1, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();
        List<ToolCard> toolCards = new ArrayList<>();

        while (resultSet.next()) {
            PreparedStatement cardPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM toolcard WHERE idtoolcard = ?");

            cardPreparedStatement.setInt(1, resultSet.getInt("idtoolcard"));

            ResultSet cardResultSet = cardPreparedStatement.executeQuery();

            if (!cardResultSet.next()) {
                break;
            }

            int idToolCard = cardResultSet.getInt("idtoolcard");

            ToolCard toolCard = CardFactory.getToolCard(
                    idToolCard,
                    cardResultSet.getString("name"),
                    cardResultSet.getString("description"),
                    this.connection
            );

            if (this.toolCardIsUsed(gameId, idToolCard)) {
                toolCard.setCost(2);
            }

            toolCards.add(toolCard);

            cardPreparedStatement.close();
            cardResultSet.close();
        }

        preparedStatement.close();
        resultSet.close();

        return toolCards;
    }

    public boolean toolCardIsUsed(int gameId, int toolCardId) throws SQLException {
        PreparedStatement statement = this.connection.getConnection().prepareStatement("SELECT if(count(*) > 0,true,false) as used FROM gamefavortoken JOIN gametoolcard ON gametoolcard.idgame = gametoolcard.idgame AND gamefavortoken.gametoolcard = gametoolcard.gametoolcard WHERE gamefavortoken.idgame = ? AND gametoolcard.idtoolcard = ?;");

        statement.setInt(1, gameId);
        statement.setInt(2, toolCardId);

        var result = statement.executeQuery();
        result.next();
        boolean bool = result.getBoolean(1);

        result.close();
        statement.close();

        return bool;
    }

    public void addMultiple(Collection<ToolCard> toolCards, int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "INSERT INTO gametoolcard (idtoolcard, idgame) VALUES (?, ?);"
        );

        var count = 0;

        for (var toolCard : toolCards) {
            preparedStatement.setInt(1, toolCard.getId());
            preparedStatement.setInt(2, gameId);

            preparedStatement.addBatch();

            ++count;

            if (count % BATCH_SIZE == 0 || count == toolCards.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    public int getGameToolCardID(int gameId, int toolCardId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("SELECT * FROM gametoolcard WHERE idtoolcard = ? AND idgame = ?;");
        preparedStatement.setInt(1, toolCardId);
        preparedStatement.setInt(2, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return 0;
        }

        final int id = resultSet.getInt("gametoolcard");

        preparedStatement.close();
        resultSet.close();

        return id;
    }

    public ToolCard getToolCardByGameToolCardId(int gameId, int gameToolCardId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("SELECT * FROM gametoolcard WHERE gametoolcard = ? AND idgame = ?;");
        preparedStatement.setInt(1, gameToolCardId);
        preparedStatement.setInt(2, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return null;
        }

        final int id = resultSet.getInt("idtoolcard");

        preparedStatement.close();
        resultSet.close();

        return this.findById(id);
    }

    public int getGameToolCardIdByToolCardId(int toolCardId, int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("SELECT gametoolcard FROM gametoolcard WHERE idtoolcard = ? AND idgame = ?;");
        preparedStatement.setInt(1, toolCardId);
        preparedStatement.setInt(2, gameId);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return 0;
        }

        final int id = resultSet.getInt("gametoolcard");

        preparedStatement.close();
        resultSet.close();

        return id;
    }

    public void addAffectedToolCard(ToolCard toolCard, List<Die> dice, int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "INSERT INTO gametoolcard_affected_gamedie VALUES (?,?,?,?)"
        );

        var count = 0;

        for (var die : dice) {
            preparedStatement.setInt(1, toolCard.getId());
            preparedStatement.setInt(2, gameId);
            preparedStatement.setInt(3, die.getNumber());
            preparedStatement.setString(4, die.getColor().getDutchColorName());

            preparedStatement.addBatch();

            ++count;

            if (count % BATCH_SIZE == 0 || count == dice.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    public boolean isGameDieAffected(int gameId, Die die) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM gametoolcard_affected_gamedie WHERE gamedie_idgame = ? AND gamedie_diecolor = ? AND gamedie_dienumber = ?;");

        preparedStatement.setInt(1, gameId);
        preparedStatement.setString(2, die.getColor().getDutchColorName());
        preparedStatement.setInt(3, die.getNumber());

        return preparedStatement.executeQuery().next();
    }
}
