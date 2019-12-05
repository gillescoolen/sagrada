package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sagrada.model.Account;
import sagrada.model.Player;

public class ChatMessageController {
    @FXML
    private Label lbMessage;
    @FXML
    private Label lbAuthor;

    private String message;
    private Player player;


    public ChatMessageController(String message, Player player) {
        this.player = player;
        this.message = message;
    }

    @FXML
    public void initialize() {
        this.lbMessage.setText(message);
        this.lbAuthor.setText(player.getAccount().getUsername());
    }
}
