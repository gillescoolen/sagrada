package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ChatMessageController {
    @FXML
    private Label lbMessage;
    @FXML
    private Label lbAuthor;

    private String message;
    private String username;


    public ChatMessageController(String message, String author) {
        this.username = author;
        this.message = message;
    }

    @FXML
    public void initialize() {
        this.lbMessage.setText(message);
        this.lbAuthor.setText(username);
    }
}
