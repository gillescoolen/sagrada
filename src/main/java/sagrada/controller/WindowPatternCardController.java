package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sagrada.database.repositories.PatternCardRepository;
import sagrada.model.PatternCard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WindowPatternCardController {
    @FXML
    private VBox window;

    private PatternCard windowField;

    private final List<Button> windowSquares = new ArrayList<>();

    public WindowPatternCardController(PatternCardRepository patternCardRepository, int windowPatternCardId) {
        try {
            this.windowField = patternCardRepository.findById(windowPatternCardId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void initialize() {
        this.fillWindow();
    }

    private void fillWindow() {
        var i = 0;
        this.initializeWindow();

        for (var square : this.windowField.getSquares()) {
            var button = this.windowSquares.get(i);
            var color = square.getColor();

            button.setText(square.getValue().toString());

            if (color != null) {
                button.setStyle("-fx-background-color: " + square.getColor().getColor());
            }

            ++i;
        }
    }

    private void initializeWindow() {
        var i = 0;

        for (Node rowNode : this.window.getChildren()) {
            if (i > 0) {
                var row = ((HBox) rowNode);

                for (Node buttonNode : row.getChildren()) {
                    var button = ((Button) buttonNode);
                    this.windowSquares.add(button);
                }
            }

            ++i;
        }
    }
}
