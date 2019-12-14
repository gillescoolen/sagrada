package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.FavorToken;
import sagrada.model.Game;
import sagrada.model.Player;

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

    public void updateFavorToken(FavorToken favorToken, int gameToolCardId, int round, boolean inFirstTurn) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("UPDATE gamefavortoken SET gametoolcard = ?, round = ?, inFirstTurn = ? WHERE idfavortoken = ?;");

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

    public boolean checkIfFavorTokensAreSet(Game game, Player player) throws SQLException {
        PreparedStatement statement = this.connection.getConnection()
                .prepareStatement("SELECT * FROM gamefavortoken WHERE idgame = ? AND idplayer = ? LIMIT 1");

        statement.setInt(1, game.getId());
        statement.setInt(2, player.getId());

        ResultSet resultSet = statement.executeQuery();

        boolean hasTokens = resultSet.next();

        statement.close();
        resultSet.close();

        return hasTokens;
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
}
