package sagrada.model.card.facade;

import javafx.scene.control.ChoiceDialog;
import sagrada.controller.GameController;
import sagrada.model.Die;
import sagrada.model.Game;
import sagrada.model.Player;

import java.util.List;
import java.util.Optional;

public class FluxBrushFacade implements ToolCardFacade {
    @Override
    public void use(GameController controller) {
        Die die = this.question(controller);

        Player player = controller.getPlayer();
        Game game = controller.getGame();
        game.getToolCards().stream()
                .filter(c -> c.getName().equals("Fluxborstel"))
                .findFirst()
                .ifPresent(card -> card.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), die));
    }

    private Die question(GameController controller) {
        List<Die> dieList = controller.getGame().getDraftPool().getDice();
        ChoiceDialog<Die> dialog = new ChoiceDialog<>(dieList.get(0), dieList);
        dialog.setTitle("Fluxborstel");
        dialog.setHeaderText("Dobbelsteen keuze");
        dialog.setContentText("Kies dobbelsteen:");

        Optional<Die> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return this.question(controller);
        }

        return result.orElse(null);
    }
}
