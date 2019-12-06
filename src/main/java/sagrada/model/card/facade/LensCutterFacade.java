package sagrada.model.card.facade;

import javafx.scene.control.ChoiceDialog;
import sagrada.controller.GameController;
import sagrada.model.Die;
import sagrada.model.Game;
import sagrada.model.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LensCutterFacade implements ToolCardFacade {
    @Override
    public void use(GameController controller) {
        Object[] message = this.question(controller);

        Player player = controller.getPlayer();
        Game game = controller.getGame();
        game.getToolCards().stream()
                .filter(c -> c.getName().equals("Rondsnijder"))
                .findFirst()
                .ifPresent(card -> card.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), message));
    }

    private Object[] question(GameController controller) {
        List<Die> dieList = controller.getGame().getDraftPool().getDice();
        ChoiceDialog<Die> dieDialog = new ChoiceDialog<>(dieList.get(0), dieList);
        dieDialog.setTitle("Rondsnijder");
        dieDialog.setHeaderText("Dobbelsteen wissel met RoundTrack");
        dieDialog.setContentText("Kies dobbelsteen:");

        Optional<Die> dieResult = dieDialog.showAndWait();
        Die chosenDraftDie = dieResult.orElse(null);

        if (chosenDraftDie == null) {
            return this.question(controller);
        }

        List<Integer> availableTracks = IntStream.rangeClosed(1, controller.getGame().getRoundTrack().getCurrent()).boxed().collect(Collectors.toList());
        ChoiceDialog<Integer> roundDialog = new ChoiceDialog<>(availableTracks.get(0), availableTracks);
        roundDialog.setTitle("Rondsnijder");
        roundDialog.setHeaderText("Dobbelsteen wissel met RoundTrack");
        roundDialog.setContentText("Kies ronde dobbelsteen:");

        Optional<Integer> roundResult = roundDialog.showAndWait();
        int chosenRound = roundResult.orElse(0);

        return new Object[]{chosenDraftDie, chosenRound};
    }
}
