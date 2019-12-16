package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.FavorToken;
import sagrada.model.Player;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class FavorTokenRepository extends Repository<FavorToken> {
    public FavorTokenRepository(DatabaseConnection connection) {
        super(connection);
    }

    @Override
    public FavorToken findById(int id) throws SQLException {
        return null;
    }

    @Override
    public void update(FavorToken model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<FavorToken> models) throws SQLException {

    }

    @Override
    public void delete(FavorToken model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<FavorToken> models) throws SQLException {

    }

    @Override
    public void add(FavorToken model) throws SQLException {

    }

    @Override
    public void addMultiple(Collection<FavorToken> models) throws SQLException {

    }

    public void initializeFavorTokens(List<FavorToken> favorTokens, int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("INSERT INTO gamefavortoken VALUES (null,?,null,null,null,null)");

        var count = 0;

        for (var token : favorTokens) {
            preparedStatement.setInt(1, gameId);

            preparedStatement.addBatch();

            ++count;

            if (count % BATCH_SIZE == 0 || count == favorTokens.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    public void updateFavorToken(FavorToken favorToken, int toolCardId, int round, boolean inFirstTurn, int gameId) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("UPDATE gamefavortoken SET gametoolcard = ?, round = ?, inFirstTurn = ? WHERE idfavortoken = ?;");

        ToolCardRepository repository = new ToolCardRepository(this.connection);
        var gameToolCardId = repository.getGameToolCardIdByToolCardId(toolCardId, gameId);

        preparedStatement.setInt(1, gameToolCardId);
        preparedStatement.setInt(2, round);
        preparedStatement.setBoolean(3, inFirstTurn);
        preparedStatement.setInt(4, favorToken.getId());

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public List<FavorToken> getFavorTokens(int gameId) throws SQLException {
        ToolCardRepository toolCardRepository = new ToolCardRepository(this.connection);

        PreparedStatement statement = this.connection.getConnection()
                .prepareStatement("SELECT * FROM gamefavortoken WHERE idgame = ? AND idplayer is null;");
        statement.setInt(1, gameId);

        ResultSet resultSet = statement.executeQuery();

        List<FavorToken> favorTokens = new ArrayList<>();

        while (resultSet.next()) {
            var id = resultSet.getInt("idfavortoken");
            var idPlayer = resultSet.getInt("idplayer");
            var round = resultSet.getInt("round");
            var gameToolCardId = resultSet.getInt("gametoolcard");

            favorTokens.add(new FavorToken(id, round, toolCardRepository.getToolCardByGameToolCardId(gameId, gameToolCardId), idPlayer));
        }

        statement.close();
        resultSet.close();

        return favorTokens;
    }

    public List<FavorToken> getPlayerFavorTokens(int gameId, int playerId) throws SQLException {
        ToolCardRepository toolCardRepository = new ToolCardRepository(this.connection);

        PreparedStatement statement = this.connection.getConnection()
                .prepareStatement("SELECT * FROM gamefavortoken WHERE idgame = ? AND idplayer = ?");

        statement.setInt(1, gameId);
        statement.setInt(2, playerId);

        ResultSet resultSet = statement.executeQuery();

        List<FavorToken> favorTokens = new ArrayList<>();

        while (resultSet.next()) {
            var id = resultSet.getInt("idfavortoken");
            var idPlayer = resultSet.getInt("idplayer");
            var round = resultSet.getInt("round");
            var gameToolCardId = resultSet.getInt("gametoolcard");

            favorTokens.add(new FavorToken(id, round, toolCardRepository.getToolCardByGameToolCardId(gameId, gameToolCardId), idPlayer));
        }

        statement.close();
        resultSet.close();

        return favorTokens;
    }

    public void updatePlayerFavorTokens(Player player, List<FavorToken> favorTokens) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "UPDATE gamefavortoken SET idplayer = ? WHERE idfavortoken = ?;"
        );

        var count = 0;

        for (var token : favorTokens) {
            preparedStatement.setInt(1, player.getId());
            preparedStatement.setInt(2, token.getId());

            preparedStatement.addBatch();

            ++count;

            if (count % BATCH_SIZE == 0 || count == favorTokens.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    public int getPlayerFavorTokensTotal(int gameId, int playerId) throws SQLException {
        PreparedStatement statement = this.connection.getConnection()
                .prepareStatement("SELECT COUNT(*) AS total FROM gamefavortoken WHERE idgame = ? AND idplayer = ? AND gametoolcard IS NULL;");

        statement.setInt(1, gameId);
        statement.setInt(2, playerId);

        ResultSet resultSet = statement.executeQuery();

        if (!resultSet.next()) {
            return 0;
        }

        final int total = resultSet.getInt("total");

        statement.close();
        resultSet.close();

        return total;
    }

    public int getFavorTokensUsed(int gameId, int toolcardId) throws SQLException {
        PreparedStatement gameToolcardIdStatement = this.connection.getConnection().prepareStatement("SELECT gametoolcard FROM gametoolcard WHERE idgame = ? AND idtoolcard = ?");

        gameToolcardIdStatement.setInt(1, gameId);
        gameToolcardIdStatement.setInt(2, toolcardId);

        ResultSet gameToolcardResultset = gameToolcardIdStatement.executeQuery();
        gameToolcardResultset.next();

        int gameToolCardId = gameToolcardResultset.getInt("gametoolcard");

        PreparedStatement statement = this.connection.getConnection()
                .prepareStatement("SELECT COUNT(*) AS total FROM gamefavortoken WHERE idgame = ? AND gametoolcard = ?;");

        statement.setInt(1, gameId);
        statement.setInt(2, gameToolCardId);

        ResultSet resultSet = statement.executeQuery();

        if (!resultSet.next()) {
            return 0;
        }

        final int total = resultSet.getInt("total");

        statement.close();
        resultSet.close();

        return total;
    }
}
