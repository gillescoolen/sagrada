package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sagrada.model.Account;

public class ChatMessageController {
    @FXML
    private Label lbMessage;
    @FXML
    private Label lbAuthor;

    private String message;
    private Account user;


    public ChatMessageController(String message, Account user) {
        this.user = user;
        this.message = message;
    }

    @FXML
    public void initialize() {
        this.lbMessage.setText(message);
        this.lbAuthor.setText(user.getUsername());
    }
}
