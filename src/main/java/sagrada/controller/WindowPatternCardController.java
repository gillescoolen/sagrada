package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class WindowPatternCardController{
    @FXML
    private VBox window;

    private final List<Button> windowSquares = new ArrayList<>();

    @FXML
    public void initialize() {
        this.initializeWindow();
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
