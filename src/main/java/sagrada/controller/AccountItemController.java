package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.AccountRepository;
import sagrada.database.repositories.GameRepository;
import sagrada.database.repositories.PlayerRepository;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountItemController {
    @FXML
    private Label lbName;
    @FXML
    private AnchorPane lobbyItem;

    private final String username;
    private final DatabaseConnection databaseConnection;

    public AccountItemController(String username, DatabaseConnection connection) {
        this.databaseConnection = connection;
        this.username = username;
    }

    @FXML
    protected void initialize() {
        this.fillItem();
        this.bindButtons();
    }

    private void fillItem() {
        this.lbName.setText(username);
    }

    private void bindButtons() {
        this.lobbyItem.setOnMouseClicked(c -> this.playerItemClicked());
    }

    private void playerItemClicked() {
        var playerRepository = new PlayerRepository(this.databaseConnection);
        var accountRepository = new AccountRepository(this.databaseConnection);

        var wins = new AtomicInteger();
        var losses = new AtomicInteger();

        try {
            var stats = accountRepository.getPlayedGameStats(this.username, playerRepository);
            var color = accountRepository.getMostUsedDieColor(username);
            stats.forEach(stat -> {
                if (stat) {
                    wins.getAndIncrement();
                } else {
                    losses.getAndIncrement();
                }
            });

            var dialog = new Dialog<>();
            dialog.getDialogPane().getButtonTypes().add(new ButtonType("Ok", ButtonBar.ButtonData.CANCEL_CLOSE));
            dialog.setTitle("Speler informatie");
            dialog.setHeaderText(String.format("Speler: %s", username));
            dialog.setContentText(String.format("" +
                            "Aantal gespeelde spellen: %s \n\n" +
                            "Aantal gewonnen spellen: %s \n\n" +
                            "Aantal verloren spellen: %s \n\n" +
                            "Meest gekozen dobbelsteen kleur: %s \n\n" +
                            "Aantal unieke tegenstanders: %s",
                    accountRepository.getPlayedGames(username),
                    wins,
                    losses,
                    ((color == null) ? "Geen kleur" : color),
                    accountRepository.getUniqueOpponents(username))
            );

            dialog.showAndWait();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
