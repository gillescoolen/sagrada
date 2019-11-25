package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.Account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class AccountRepository extends Repository<Account> {

    public AccountRepository(DatabaseConnection connection) {
        super(connection);
    }

    public Account findByUsername(String username) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM account WHERE username = ?");

        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.getFetchSize() > 1) {
            throw new SQLException("Multiple results, expected 1.");
        }

        final String usernameAccount = resultSet.getString("username");
        final String password = resultSet.getString("password");

        resultSet.close();
        preparedStatement.close();

        return new Account(username, password);
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
