package sagrada.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.ChatRepository;
import sagrada.model.ChatLine;
import sagrada.model.Game;
import sagrada.model.Player;

import java.io.IOException;
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
    private ListView<String> lvMessageBox;

    private final Game game;
    private final Player player;
    private final ChatRepository chatRepository;
    private final Timer getMessagesTimer = new Timer();

    public ChatController(DatabaseConnection databaseConnection, Player player, Game game) {
        this.game = game;
        this.player = player;
        this.chatRepository = new ChatRepository(databaseConnection);
    }

    @FXML
    public void initialize() {
        this.sendButton.setOnMouseClicked(c -> sendMessage(this.messageField.getText()));
        this.messageField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    sendMessage(messageField.getText());
                }
            }
        });

        this.getMessagesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getMessages();
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
            message = message.trim();
            if (message.length() > 0 && !message.isBlank()) {
                this.chatRepository.add(new ChatLine(this.player, message));
                this.messageField.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all unfetched messages from the database.
     */
    private void getMessages() {
        try {
            List<String> lines = this.chatRepository.getMultiple(this.game.getId());

            Platform.runLater(() -> {
                this.lvMessageBox.getItems().clear();

                for (String line : lines) {
                    this.lvMessageBox.getItems().add(line);
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

