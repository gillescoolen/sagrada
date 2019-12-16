package sagrada.database.repositories;

import javafx.util.Pair;
import sagrada.database.DatabaseConnection;
import sagrada.model.Account;
import sagrada.model.PatternCard;
import sagrada.model.PlayStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class AccountRepository extends Repository<Account> {

    public AccountRepository(DatabaseConnection connection) {
        super(connection);
    }

    public Account findByUsername(String username) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM account WHERE username = ?;");

        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.getFetchSize() > 1) {
            throw new SQLException("Multiple results, expected 1.");
        }

        if (!resultSet.next()) {
            return null;
        }

        final String usernameAccount = resultSet.getString("username");
        final String password = resultSet.getString("password");

        resultSet.close();
        preparedStatement.close();

        return new Account(usernameAccount, password);
    }

    public List<String> getAllAccounts() throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT username FROM account");
        ResultSet resultSet = preparedStatement.executeQuery();

        List<String> accounts = new ArrayList<>();

        while (resultSet.next()) {
        var accountName = resultSet.getString("username");
        accounts.add(accountName);
    }

        resultSet.close();
        preparedStatement.close();

        return accounts;
    }

    public Account getUserByUsernameAndPassword(String username, String password) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM account WHERE username = ? AND password = ?");

        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.getFetchSize() > 1) {
            throw new SQLException("Multiple results, expected 1.");
        }

        if (!resultSet.next()) {
            return null;
        }

        final String usernameAccount = resultSet.getString("username");
        final String passwordAccount = resultSet.getString("password");

        resultSet.close();
        preparedStatement.close();

        return new Account(usernameAccount, passwordAccount);
    }

    public int getPlayedGames(String username) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT COUNT(*) AS wins FROM player WHERE username = ? AND playstatus_playstatus = ?");

        preparedStatement.setString(1, username);
        preparedStatement.setString(2, PlayStatus.DONE_PLAYING.getPlayState());

        var resultSet = preparedStatement.executeQuery();
        resultSet.next();
        var wins = resultSet.getInt("wins");

        resultSet.close();
        preparedStatement.close();

        return wins;
    }

    public List<Boolean> getPlayedGameStats(String username, PlayerRepository playerRepository) throws SQLException {
        var stats = new ArrayList<Boolean>();

        PreparedStatement statement = this.connection.getConnection().prepareStatement("select spel_idspel from player where username = ? and playstatus_playstatus = ?");

        statement.setString(1, username);
        statement.setString(2, PlayStatus.DONE_PLAYING.getPlayState());

        var resultSet = statement.executeQuery();

        while (resultSet.next()) {
            var id = resultSet.getInt("spel_idspel");
            var players = playerRepository.getFinishedGamePlayers(id);

            var winner = players.stream().max(Comparator.comparing(Pair::getValue)).orElse(null);

            if (winner != null) {
                var score = winner.getKey().equals(username);
                stats.add(score);
            }
        }

        resultSet.close();
        statement.close();

        return stats;
    }

    public String getMostUsedDieColor(String username) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT diecolor, MAX(test) FROM (SELECT diecolor, count(diecolor) as test FROM player p INNER JOIN gamedie g ON p.spel_idspel = g.idgame where username = ? group by diecolor) as pgt");

        preparedStatement.setString(1, username);

        var resultSet = preparedStatement.executeQuery();
        resultSet.next();
        var color = resultSet.getString("diecolor");

        resultSet.close();
        preparedStatement.close();

        return color;
    }

    public Integer getUniqueOpponents(String username) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT COUNT(DISTINCT p.username) AS count FROM player p WHERE username != ? AND p.playstatus_playstatus IN ('uitdager', 'geaccepteerd', 'klaar') AND p.spel_idspel IN (SELECT spel_idspel FROM player c WHERE c.username = ?);");

        preparedStatement.setString(1, username);
        preparedStatement.setString(2, username);

        var resultSet = preparedStatement.executeQuery();
        resultSet.next();
        var opponents = resultSet.getInt("count");

        resultSet.close();
        preparedStatement.close();

        return opponents;
    }

    @Override
    public Account findById(int id) throws SQLException {
        throw new SQLException("Account has no id");
    }

    @Override
    public void update(Account model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<Account> models) throws SQLException {

    }

    @Override
    public void delete(Account model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<Account> models) throws SQLException {

    }

    @Override
    public void add(Account model) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("INSERT INTO account values(?, ?);");

        preparedStatement.setString(1, model.getUsername());
        preparedStatement.setString(2, model.getPassword());

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    @Override
    public void addMultiple(Collection<Account> models) throws SQLException {

    }
}
