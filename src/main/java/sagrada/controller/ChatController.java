package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.ChatRepository;
import sagrada.model.ChatLine;
import sagrada.model.Game;
import sagrada.model.Player;
import sagrada.util.ChatLinePair;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatController {
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private ListView<Node> lvMessageBox;

    private LocalDateTime lastFetched;

    private final Game game;
    private final Player player;
    private final ChatRepository chatRepository;
    private final Timer getMessagesTimer = new Timer();

    public ChatController(DatabaseConnection databaseConnection, Player player, Game game) {
        this.game = game;
        this.player = player;
        this.chatRepository = new ChatRepository(databaseConnection);
        this.lastFetched = game.getCreatedOn();
    }

    @FXML
    public void initialize() {
        this.sendButton.setOnMouseClicked(c -> sendMessage(this.messageField.getText()));

        this.getMessages();

        this.getMessagesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> getMessages());
            }
        }, 0, 2000);
    }

    /**
     * Send a message to the database.
     *
     * @param message The message the user wrote.
     */
    private void sendMessage(String message) {
        try {
            this.chatRepository.add(new ChatLine(player, LocalDateTime.now(), message));
            this.messageField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a message tot the chatbox.
     *
     * @param message  The message the user wrote.
     * @param username The name of the user that wrote the message.
     */
    private void addMessage(String message, String username) throws IOException {
        URL template = this.getClass().getResource("/views/chat/chatMessage.fxml");
        FXMLLoader loader = new FXMLLoader(template);
        loader.setController(new ChatMessageController(message, username));
        lvMessageBox.getItems().add(loader.load());
    }

    /**
     * Get all unfetched messages from the database.
     */
    private void getMessages() {
        try {
            List<ChatLinePair> lines = this.chatRepository.getMultiple(this.lastFetched, this.game.getId());

            for (ChatLinePair line : lines) {
                this.addMessage(line.getMessage(), line.getUsername());
            }

            this.lastFetched = LocalDateTime.now();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}

