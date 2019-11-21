package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sagrada.database.DatabaseConnection;

import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField tfUsername;

    @FXML
    private PasswordField pfPassword;

    @FXML
    protected void handleLogin() {
        try {
            var validLogin = this.doLogin();

            if (validLogin) {
                System.out.println("Logged in");
            } else {
                System.out.println("Invalid login");
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong!");
        }
    }

    private boolean doLogin() throws SQLException {
        var connection = new DatabaseConnection();

        connection.connect();

        var database = connection.getConnection();
        var preparedStatement = database.prepareStatement("SELECT * FROM account WHERE username = ? AND password = ?");

        preparedStatement.setString(1, this.tfUsername.getText());
        preparedStatement.setString(2, this.pfPassword.getText());

        var resultSet = preparedStatement.executeQuery();

        return resultSet.getFetchSize() == 1;
    }
}
