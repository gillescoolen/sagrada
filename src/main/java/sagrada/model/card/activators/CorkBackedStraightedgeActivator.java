package sagrada.model.card.activators;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import sagrada.controller.GameController;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CorkBackedStraightedgeActivator extends ToolCardActivator {
    CorkBackedStraightedgeActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public boolean activate() throws SQLException {
        Player player = this.controller.getPlayer();
        Game game = this.controller.getGame();
        Pair<Die, Square> move;

        List<Square> availableSquares = player.getPlayerFrame().getSquares().stream().filter(square -> square.getDie() == null).collect(Collectors.toList());

        var dialog = this.question(availableSquares);
        Optional<Pair<Die, Square>> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return false;
        } else {
            move = result.get();
        }

        this.toolCard.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), player, game, move);

        return true;
    }

    private Dialog<Pair<Die, Square>> question(List<Square> availableSquares) {
        var dieList = this.controller.getGame().getDraftPool().getDice();
        Dialog<Pair<Die, Square>> dialog = new Dialog<>();

        dialog.setTitle("Snijliniaal");
        dialog.setHeaderText("Dobbelsteen keuze");
        dialog.setContentText("Kies dobbelsteen:");

        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Die> squareComboBox1 = new ComboBox<>();
        squareComboBox1.getItems().addAll(dieList);

        grid.add(new Label("Dobbelsteen:"), 0, 0);
        grid.add(squareComboBox1, 1, 0);

        ComboBox<Square> squareComboBox2 = new ComboBox<>();
        squareComboBox2.getItems().addAll(availableSquares);

        grid.add(new Label("Kies een plek:"), 0, 1);
        grid.add(squareComboBox2, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                var value1 = squareComboBox1.getValue();
                var value2 = squareComboBox2.getValue();

                if (value1 == null || value2 == null) {
                    return null;
                }

                if (value1.equals(value2)) {
                    return null;
                }

                return new Pair<>(value1, value2);
            }

            return null;
        });

        return dialog;
    }
}
