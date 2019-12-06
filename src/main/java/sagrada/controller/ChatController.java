package sagrada.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.ChatRepository;
import sagrada.model.ChatLine;
import sagrada.model.Game;
import sagrada.model.Player;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Timer;

public class ChatController {
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private ListView lvMessageBox;

    private final Game game;
    private final Player player;

    private final Timer getMessages = new Timer();

    private final ChatRepository chatRepository;
    private final DatabaseConnection databaseConnection;

    public ChatController(DatabaseConnection databaseConnection, Player player, Game game) {
        this.game = game;
        this.player = player;
        this.databaseConnection = databaseConnection;
        this.chatRepository = new ChatRepository(databaseConnection);
    }

    @FXML
    public void initialize() {
        this.sendButton.setOnMouseClicked(c -> sendMessage(this.messageField.getText()));
    }

    /**
     * Send a message to the database and to our client's chatbox.
     *
     * @param message The message the user wrote.
     */
    private void sendMessage(String message) {
        try {
            this.chatRepository.add(new ChatLine(player, LocalDateTime.now(), message));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.addMessage(message, this.player);
    }

    /**
     * Add a message tot the chatbox.
     *
     * @param message The message the user wrote.
     * @param author  The user that wrote the message.
     */
    private void addMessage(String message, Player author) {
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
