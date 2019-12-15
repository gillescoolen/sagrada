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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class TapWheelActivator extends ToolCardActivator {
    private Player player;
    private final static int SQUARES_TO_MOVE = 2;

    public TapWheelActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public boolean activate() throws SQLException {
        this.player = this.controller.getPlayer();
        Game game = this.controller.getGame();

        RoundTrack roundTrack = game.getRoundTrack();
        Map<Integer, Die> trackDice = roundTrack.getTrack();

        if (!checkIfCardCanBeUsed(trackDice)) {
            this.showMessage("De kaart kan niet gebruikt worden, er word niet voldaan aan de vereisten (bijv. de ronde track)");
            return false;
        }

        List<Pair<Square, Square>> dieToMovePair = this.askWhichDiceToMove(trackDice);

        if (dieToMovePair == null) {
            this.showMessage("De kaart kan niet uitgevoerd worden");
            return false;
        }

        this.toolCard.use(game.getDraftPool(), this.player.getDiceBag(), this.player.getPatternCard(), game.getRoundTrack(), player, game, dieToMovePair);

        return true;
    }

    // FIXME: fix this in the future
    private boolean checkIfCardCanBeUsed(Map<Integer, Die> trackDice) {
        List<Boolean> matches = new ArrayList<>();
        for (Map.Entry<Integer, Die> entry : trackDice.entrySet()) {
            if (entry.getValue().getColor() == null || entry.getValue().getValue() == null) {
                matches.add(false);
            } else {
                matches.add(true);
            }
        }

        int matchesCount = (int) matches.stream().filter(bool -> bool).count();

        if (matchesCount < 2) {
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error dialoog");
        alert.setContentText(message);

        alert.showAndWait();
    }

    private List<Pair<Square, Square>> askWhichDiceToMove(Map<Integer, Die> roundtrackDie) {

        List<Square> dice = this.player.getPlayerFrame().getSquares().stream()
                .filter(square -> square.getDie() != null)
                .filter(square -> roundtrackDie.entrySet().stream().anyMatch(pair -> pair.getValue().getColor() == square.getDie().getColor()))
                .collect(Collectors.toList());

        if (dice.size() == 0) {
            return null;
        }

        List<Square> availableSquares = this.player.getPlayerFrame().getSquares().stream().filter(square -> square.getDie() == null).collect(Collectors.toList());

        List<Pair<Square, Square>> squares = new ArrayList<>(SQUARES_TO_MOVE);

        for (int i = 0; i < SQUARES_TO_MOVE; i++) {
            var dialog = this.createDialog(dice, availableSquares, i);
            Optional<Pair<Square, Square>> result = dialog.showAndWait();

            if (result.isEmpty()) {
                i = i - 1;
            } else {
                var input = result.get();

                if (!this.isValidInput(squares, input)) {
                    this.showInvalidMessage();
                    i = i - 1;
                } else {
                    squares.add(input);
                }
            }
        }
        return squares;
    }

    private void showInvalidMessage() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("U heeft een dobbelsteen of positie gekozen die al eerder gekozen is");
        alert.setContentText("Kies opnieuw");

        alert.showAndWait();
    }

    private boolean isValidInput(List<Pair<Square, Square>> squares, Pair<Square, Square> result) {
        for (var square : squares) {
            if (square.getKey() == result.getKey() || square.getValue() == result.getValue()) {
                return false;
            }
        }

        return true;
    }

    private Dialog<Pair<Square, Square>> createDialog(List<Square> availableDice, List<Square> availableSquares, int i) {
        Dialog<Pair<Square, Square>> dialog = new Dialog<>();
        dialog.setTitle("Loodopenhaler " + (i + 1) + "/" + 2);

        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Square> squareComboBox1 = new ComboBox<>();
        squareComboBox1.getItems().addAll(availableDice);

        grid.add(new Label("Dobbelsteen die verplaatst moet worden "), 0, 0);
        grid.add(squareComboBox1, 1, 0);

        ComboBox<Square> squareComboBox2 = new ComboBox<>();
        squareComboBox2.getItems().addAll(availableSquares);

        grid.add(new Label("Vervangende plek"), 0, 1);
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
