package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.AccountRepository;
import sagrada.model.Account;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label messageLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    private AccountRepository accountRepository;

    @FXML
    protected void initialize() {
        this.loginButton.setOnAction((actionEvent) -> this.handleLogin());
        this.registerButton.setOnAction((actionEvent) -> this.handleRegister());

        try {
            var connection = new DatabaseConnection();
            this.accountRepository = new AccountRepository(connection);

            connection.connect();
        } catch (SQLException e) {
            this.registerButton.setDisable(true);
            this.loginButton.setDisable(true);
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Er kon geen database connectie gemaakt worden");
        }
    }

    private void handleLogin() {
        this.resetUi();

        try {
            var account = this.accountRepository.getUserByUsernameAndPassword(this.tfUsername.getText(), this.pfPassword.getText());

            if (account != null) {
                try {
                    this.switchScene(account);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                this.messageLabel.getStyleClass().add("warning");
                this.messageLabel.setText("Gebruikersnaam/wachtwoord is verkeerd!");
            }
        } catch (SQLException e) {
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Er ging iets fout tijdens het inloggen!");
        }
    }

    private void handleRegister() {
        this.resetUi();

        try {
            var account = new Account(this.tfUsername.getText(), this.pfPassword.getText());
            this.accountRepository.add(account);
        } catch (SQLException e) {
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Er ging iets fout tijdens het registreren!");
        }

        this.messageLabel.getStyleClass().add("success");
        this.messageLabel.setText("Registreren is gelukt!");
    }

    private void resetUi() {
        this.registerButton.setDisable(false);
        this.loginButton.setDisable(false);
        this.messageLabel.getStyleClass().clear();
        this.messageLabel.setText("");
    }

    private void switchScene(Account account) throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/views/lobby.fxml"));
        var stage = ((Stage) this.tfUsername.getScene().getWindow());
        loader.setController(new LobbyController(account));
        var scene = new Scene(loader.load());
        stage.setScene(scene);
    }
}
