package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.model.Game;

import java.sql.SQLException;
import java.util.Optional;


public class GameController {
    @FXML
    private VBox rowOne;
    @FXML
    private VBox rowTwo;

    @FXML
    private Button btnInvite;

    private final Game game;
    private final DatabaseConnection connection;

    GameController(DatabaseConnection connection, Game game) {
        this.game = game;
        this.connection = connection;
    }

    @FXML
    protected void initialize() {
        this.btnInvite.setOnMouseClicked(e -> inviteOthers());
    }

    private void inviteOthers() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Invite others");
        dialog.setContentText("Please enter the name of the person you'd like to invite:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(playerName -> {
            try {
                this.game.getOwner().inviteOtherPlayer(playerName, new PlayerRepository(this.connection), this.game);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Invited player");
                alert.setContentText("You have successfully invited player: " + playerName);

                alert.showAndWait();
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setContentText("Could not invite another player");

                alert.showAndWait();

                e.printStackTrace();
            }
        });

    }
}
