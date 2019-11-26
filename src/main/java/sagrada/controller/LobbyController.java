package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import sagrada.model.Account;

public class LobbyController {
    private final Account user;

    @FXML
    private VBox vbLobbyItems;

    public LobbyController(Account account) {
        this.user = account;
    }

    @FXML
    protected void initialize() {

    }
}
