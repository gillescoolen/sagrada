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

public final class TapWheelActivator extends ToolCardActivator {
    private int amountOfDice = 0;

    public TapWheelActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public void activate() throws SQLException {
        List<Pair<Square, Square>> squarePairList = this.askWhichDiceToMove();

        Player player = this.controller.getPlayer();
        Game game = this.controller.getGame();

        this.toolCard.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), player, game, squarePairList);
    }

    private int askHowManyDice() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(1, 2);
        dialog.setTitle("Olieglassnijder");
        dialog.setHeaderText("Hoeveelheid dobbelstenen");
        dialog.setContentText("Kies een hoeveelheid dobbelstenen:");

        Optional<Integer> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return this.askHowManyDice();
        }

        return result.get();
    }

    private List<Pair<Square, Square>> askWhichDiceToMove() {
        Player player = this.controller.getPlayer();
        Game game = this.controller.getGame();

        this.amountOfDice = this.askHowManyDice();

        List<Pair<Square, Square>> squares = new ArrayList<>(this.amountOfDice);

        RoundTrack roundTrack = game.getRoundTrack();

        // FIXME: check if this works later on
        // I feel like it just grabs a null because there is no current die on the roundtrack.
        Die lastRoundtrackDie = roundTrack.getDieByKey(roundTrack.getCurrent());

        List<Square> availableDice = player.getPlayerFrame()
                .getSquares()
                .stream()
                .filter(square -> square.getColor() == lastRoundtrackDie.getColor()).collect((Collectors.toList()));

        for (int i = 0; i < this.amountOfDice; i++) {

            var dialog = this.createDialog(availableDice, i);
            Optional<Pair<Square, Square>> result = dialog.showAndWait();

            if (result.isEmpty()) {
                i = i - 1; // TODO: test if this works.
            } else {
                squares.add(result.get());
            }
        }

        return squares;
    }

    private Dialog<Pair<Square, Square>> createDialog(List<Square> availableDice, int i) {
        Dialog<Pair<Square, Square>> dialog = new Dialog<>();
        dialog.setTitle("Olieglassnijder " + i + 1 + "/" + this.amountOfDice + 1);

        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.OK);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Square> squareComboBox1 = new ComboBox<>();
        squareComboBox1.getItems().addAll(availableDice);

        grid.add(new Label("Dice 1: "), 0, 0);
        grid.add(squareComboBox1, 1, 0);

        ComboBox<Square> squareComboBox2 = new ComboBox<>();
        squareComboBox1.getItems().addAll(availableDice);

        grid.add(new Label("Die 2: "), 0, 1);
        grid.add(squareComboBox2, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Pair<>(squareComboBox1.getValue(), squareComboBox2.getValue());
            }

            return null;
        });

        return dialog;
    }
}
