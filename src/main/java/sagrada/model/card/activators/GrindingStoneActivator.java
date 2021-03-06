package sagrada.model.card.activators;

import javafx.scene.control.ChoiceDialog;
import sagrada.controller.GameController;
import sagrada.model.Die;
import sagrada.model.Game;
import sagrada.model.Player;
import sagrada.model.ToolCard;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class GrindingStoneActivator extends ToolCardActivator {
    GrindingStoneActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public boolean activate() throws SQLException {
        Die die = this.question();

        Player player = this.controller.getPlayer();
        Game game = this.controller.getGame();
        return this.toolCard.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), player, game, die);
    }

    private Die question() {
        List<Die> dieList = this.controller.getGame().getDraftPool().getDice();

        ChoiceDialog<Die> dialog = new ChoiceDialog<>(dieList.get(0), dieList);
        dialog.setTitle("Schuurblok");
        dialog.setHeaderText("Dobbelsteen keuze");
        dialog.setContentText("Kies dobbelsteen:");

        Optional<Die> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return this.question();
        }

        return result.get();
    }
}
