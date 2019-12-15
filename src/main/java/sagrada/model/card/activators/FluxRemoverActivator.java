package sagrada.model.card.activators;

import javafx.scene.control.ChoiceDialog;
import sagrada.controller.GameController;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.Optional;

public final class FluxRemoverActivator extends ToolCardActivator {
    public FluxRemoverActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public boolean activate() throws SQLException {
        Player player = this.controller.getPlayer();
        Game game = this.controller.getGame();
        Die die = this.askDieFromDraft();

        int newValue = this.askValue();

        Object[] messages = new Object[2];
        messages[0] = die;
        messages[1] = newValue;

        this.toolCard.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), player, game, messages);

        return true;
    }

    private Die askDieFromDraft() {
        DraftPool draftPool = this.controller.getGame().getDraftPool();

        ChoiceDialog<Die> dialog = new ChoiceDialog<>(draftPool.getDice().get(0), draftPool.getDice());
        dialog.setTitle("Fluxverwijderaar 1/2");
        dialog.setHeaderText("Dobbelsteen keuze");
        dialog.setContentText("Kies dobbelsteen:");

        Optional<Die> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return this.askDieFromDraft();
        }

        return result.get();
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
