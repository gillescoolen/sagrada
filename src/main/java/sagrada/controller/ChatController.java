package sagrada.controller;

import javafx.application.Platform;
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
    private ListView<ChatLine> lvMessageBox;

    private LocalDateTime lastFetched;

    private final Game game;
    private final Player player;

    private final Timer getMessagesTimer = new Timer();

    private final ChatRepository chatRepository;

    public ChatController(DatabaseConnection databaseConnection, Player player, Game game) {
        this.game = game;
        this.player = player;
        this.lastFetched = LocalDateTime.now();
        this.chatRepository = new ChatRepository(databaseConnection);
    }

    @FXML
    public void initialize() {
        this.sendButton.setOnMouseClicked(c -> sendMessage(this.messageField.getText()));
        this.getMessagesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> getMessages());
            }
        }, 0, 5000);

    }

    /**
     * Send a message to the database and to our client's chatbox.
     *
     * @param message The message the user wrote.
     */
    private void sendMessage(String message) {
        try {
            this.addMessage(message, this.player.getAccount().getUsername());
            this.chatRepository.add(new ChatLine(player, LocalDateTime.now(), message));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a message tot the chatbox.
     *
     * @param message The message the user wrote.
     * @param author  The user that wrote the message.
     */
    private void addMessage(String message, String author) throws IOException {
        URL template = this.getClass().getResource("/views/chat/chatMessage.fxml");
        FXMLLoader loader = new FXMLLoader(template);
        loader.setController(new ChatMessageController(message, author));
        lvMessageBox.getItems().add(loader.load());
    }

    /**
     * Get all unfetched messages from the database.
     */
    private void getMessages() {
        try {
            List<ChatLinePair> lines = this.chatRepository.getMultiple(this.lastFetched, this.game.getId());
            this.lastFetched = LocalDateTime.now();

            for (ChatLinePair line : lines) {
                try {
                    this.addMessage(line.getMessage(), line.getUsername());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

