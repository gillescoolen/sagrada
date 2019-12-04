package sagrada.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.GameRepository;
import sagrada.model.Account;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;

public class ChatController {
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private ListView lvMessageBox;

    private final Account user;
    private final Timer getMessages = new Timer();
    private final DatabaseConnection databaseConnection;

    public ChatController(DatabaseConnection databaseConnection, Account user ) {
        this.user = user;
        this.databaseConnection = databaseConnection;
    }

    @FXML
    public void initialize() {
        this.sendButton.setOnMouseClicked(c -> sendMessage(this.messageField.getText()));
    }

    /**
     * Send a message to the database and to our client's chatbox.
     * @param message The message the user wrote.
     */
    private void sendMessage(String message) {
        // TODO: Send message to DB
        this.addMessage(message, this.user);
    }

    /**
     * Add a message tot the chatbox.
     * @param message The message the user wrote.
     * @param author The user that wrote the message.
     */
    private void addMessage(String message, Account author) {
        try {
            URL template = this.getClass().getResource("/views/chat/chatMessage.fxml");
            FXMLLoader loader = new FXMLLoader(template);
            loader.setController(new ChatMessageController(message, author));
            lvMessageBox.getItems().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
