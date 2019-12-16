package sagrada.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sagrada.component.LobbyScreen;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.AccountRepository;
import sagrada.model.Account;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private VBox vbLogin;
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

    private final AccountRepository accountRepository;
    private final DatabaseConnection databaseConnection;

    public LoginController(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.accountRepository = new AccountRepository(databaseConnection);
    }

    @FXML
    protected void initialize() {
        this.loginButton.setOnAction((actionEvent) -> this.handleLogin());
        this.vbLogin.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    handleLogin();
                }
            }
        });
        this.registerButton.setOnAction((actionEvent) -> this.handleRegister());

        try {
            this.databaseConnection.connect();
        } catch (SQLException e) {
            this.registerButton.setDisable(true);
            this.loginButton.setDisable(true);
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Er kon geen database connectie gemaakt worden");
        }
    }

    private void handleLogin() {
        this.resetUi();

        var username = this.tfUsername.getText();
        var password = this.pfPassword.getText();

        if (username.equals("") || password.equals("")) {
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Gebruikersnaam of wachtwoord is niet ingevuld!");
        } else {
            try {
                var account = this.accountRepository.getUserByUsernameAndPassword(username, password);

                if (account != null) {
                    try {
                        this.switchScene(account);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    this.messageLabel.getStyleClass().add("warning");
                    this.messageLabel.setText("Combinatie gebruikersnaam en wachtwoord is niet juist!");
                }
            } catch (SQLException e) {
                this.messageLabel.getStyleClass().add("warning");
                this.messageLabel.setText("Er ging iets fout tijdens het inloggen!");
            }
        }
    }

    private void handleRegister() {
        this.resetUi();

        var username = this.tfUsername.getText();
        var password = this.pfPassword.getText();

        if (!username.matches("([A-Za-z0-9]{3,})+") || !password.matches("([A-Za-z0-9]{3,})+")) {
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Je mag alleen letters en cijfers in je gebruikersnaam/wachtwoord gebruiken!");

            return;
        }

        if (username.equals("") || password.equals("")) {
            this.messageLabel.getStyleClass().add("warning");
            this.messageLabel.setText("Gebruikersnaam of wachtwoord is niet ingevuld!");
        } else {
            try {
                var account = new Account(username, password);
                this.accountRepository.add(account);

                this.messageLabel.getStyleClass().add("success");
                this.messageLabel.setText("Registreren is gelukt!");
            } catch (SQLException e) {
                this.messageLabel.getStyleClass().add("warning");
                this.messageLabel.setText("Er ging iets fout tijdens het registreren!");
            }
        }

    }

    private void resetUi() {
        this.registerButton.setDisable(false);
        this.loginButton.setDisable(false);
        this.messageLabel.getStyleClass().clear();
        this.messageLabel.setText("");
    }

    private void switchScene(Account account) throws IOException {
        var stage = ((Stage) this.tfUsername.getScene().getWindow());
        var scene = new Scene(new LobbyScreen(this.databaseConnection, account).load());
        stage.setScene(scene);
    }
}
