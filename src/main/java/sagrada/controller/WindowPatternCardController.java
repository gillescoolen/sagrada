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
import java.util.function.Consumer;

public class WindowPatternCardController implements Consumer<PatternCard> {
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

    @Override
    public void accept(PatternCard patternCard) {
        this.windowField = patternCard;
        this.fillWindow();
    }

    @FXML
    protected void initialize() {
        this.initializeWindow();
        this.fillWindow();

        if (this.windowField.getId() != 0) {
            // init timer here
        }
    }

    private void fillWindow() {
        var i = 0;

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
