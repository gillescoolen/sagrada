package sagrada.model.card.activators;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import sagrada.controller.GameController;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.Optional;

public final class GrozingPliersActivator extends ToolCardActivator {
    public GrozingPliersActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public void activate() throws SQLException {
        Player player = this.controller.getPlayer();
        Game game = this.controller.getGame();
        Die oldDie = this.askWhichDieToBeChanged();

        Integer newDieValue = this.askIncreaseOrDecreaseDieValue(oldDie);

        Object[] message = new Object[2];
        message[0] = oldDie;
        message[1] = newDieValue;

        this.toolCard.use(game.getDraftPool(), this.controller.getPlayer().getDiceBag(), this.controller.getPlayer().getPatternCard(), game.getRoundTrack(), player, game, message);
    }

    private Die askWhichDieToBeChanged() {
        DraftPool draftPool = this.controller.getGame().getDraftPool();

        ChoiceDialog<Die> dialog = new ChoiceDialog<>(draftPool.getDice().get(0), draftPool.getDice());
        dialog.setTitle("Driepuntstang 1/2");
        dialog.setHeaderText("Dobbelsteen keuze");
        dialog.setContentText("Kies dobbelsteen:");

        Optional<Die> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return this.askWhichDieToBeChanged();
        }

        return result.get();
    }

    private int askIncreaseOrDecreaseDieValue(Die die) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog with Custom Actions");
        alert.setHeaderText("Look, a Confirmation Dialog with Custom Actions");
        alert.setContentText("Choose your option.");

        ButtonType buttonIncrease = new ButtonType("Increase value by 1");
        ButtonType buttonDecrease = new ButtonType("Decrease value by 1");
        alert.getButtonTypes().setAll(buttonIncrease, buttonDecrease);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty()) {
            return this.askIncreaseOrDecreaseDieValue(die);
        }

        int newValue = die.getValue();

        if (result.get() == buttonIncrease) {
            newValue = newValue + 1;
        } else if (result.get() == buttonDecrease) {
            newValue = newValue - 1;
        }

        boolean dieValueIsValid = this.validateValueChoice(newValue);

        if (!dieValueIsValid) {
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setTitle("Error Dialog");
            alert2.setHeaderText("Your value choice was invalid");
            alert2.setContentText("Try again");

            alert2.showAndWait();

            return this.askIncreaseOrDecreaseDieValue(die);
        }

        return newValue;
    }

    private boolean validateValueChoice(int newValue) {
        if (newValue > 6) {
            return false;
        }

        return newValue >= 1;
    }
}
