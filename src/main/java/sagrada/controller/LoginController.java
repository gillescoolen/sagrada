package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.AccountRepository;
import sagrada.model.Account;

import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label messageLabel;

    private AccountRepository accountRepository;

    @FXML
    protected void initialize() {
        try {
            var connection = new DatabaseConnection();
            var accountRepository = new AccountRepository(connection);

            connection.connect();
            this.accountRepository = accountRepository;
        } catch (SQLException e) {
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Er kon geen database connectie gemaakt worden");
        }
    }

    @FXML
    protected void handleLogin() {
        this.resetMessage();

        try {
            var loggedIn = this.accountRepository.getUserByUsernameAndPassword(this.tfUsername.getText(), this.pfPassword.getText());

            if (loggedIn != null) {
                this.messageLabel.getStyleClass().add("success");
                this.messageLabel.setText("Ingelogd!");
            } else {
                this.messageLabel.getStyleClass().add("warning");
                this.messageLabel.setText("Gebruikersnaam/wachtwoord is verkeerd!");
            }
        } catch (SQLException e) {
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Er ging iets fout tijdens het inloggen!");
        }
    }

    @FXML
    protected void handleRegister() {
        this.resetMessage();

        try {
            var account = new Account(this.tfUsername.getText(), this.pfPassword.getText());
            this.accountRepository.add(account);
        } catch (SQLException e) {
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Er ging iets fout tijdens het registreren!");
        } finally {
            this.messageLabel.getStyleClass().add("success");
            this.messageLabel.setText("Registreren is gelukt!");
        }
    }

    private void resetMessage() {
        this.messageLabel.getStyleClass().clear();
        this.messageLabel.setText("");
    }
}
