package sagrada.model.card.activators;

import javafx.scene.control.ChoiceDialog;
import sagrada.controller.GameController;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class FluxRemoverActivator extends ToolCardActivator {
    private final Game game;

    public FluxRemoverActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
        this.game = gameController.getGame();
    }

    @Override
    public void activate() throws SQLException {
        Die die = this.askDieFromDraft();

        Player player = this.controller.getPlayer();

        int newValue = this.askValue();

        Object[] messages = new Object[2];
        messages[0] = die;
        messages[1] = newValue;

        this.toolCard.use(this.game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), this.game.getRoundTrack(), player, this.game, messages);
    }

    private Die askDieFromDraft() {
        List<Die> dieList = this.game.getDraftPool().getDice();
        ChoiceDialog<Die> dialog = new ChoiceDialog<>(dieList.get(0), dieList);
        dialog.setTitle("Fluxverwijderaar 1/2");
        dialog.setHeaderText("Dobbelsteen keuze");
        dialog.setContentText("Kies dobbelsteen:");

        Optional<Die> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return this.askDieFromDraft();
        }

        return result.orElse(null);
    }

    private int askValue() {
        Integer[] choices = new Integer[] { 1, 2, 3, 4, 5, 6};
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(choices[0], choices);
        dialog.setTitle("Fluxverwijderaar 2/2");
        dialog.setHeaderText("Kies een waarde");
        dialog.setContentText("Kies een waarde: ");

        Optional<Integer> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return this.askValue();
        }

        return result.get();
    }
}
