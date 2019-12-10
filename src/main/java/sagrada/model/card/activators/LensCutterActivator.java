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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class LensCutterActivator extends ToolCardActivator {
    LensCutterActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public void activate() throws SQLException {
        Object[] message = this.question();

        Player player = this.controller.getPlayer();
        Game game = this.controller.getGame();
        this.toolCard.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), player, game, message);
    }

    private Object[] question() {
        List<Die> dieList = this.controller.getGame().getDraftPool().getDice();
        ChoiceDialog<Die> dieDialog = new ChoiceDialog<>(dieList.get(0), dieList);
        dieDialog.setTitle("Rondsnijder");
        dieDialog.setHeaderText("Dobbelsteen wissel met RoundTrack");
        dieDialog.setContentText("Kies dobbelsteen:");

        Optional<Die> dieResult = dieDialog.showAndWait();
        Die chosenDraftDie = dieResult.orElse(null);

        if (chosenDraftDie == null) {
            return this.question();
        }

        List<Integer> availableTracks = IntStream.rangeClosed(1, this.controller.getGame().getRoundTrack().getCurrent()).boxed().collect(Collectors.toList());
        ChoiceDialog<Integer> roundDialog = new ChoiceDialog<>(availableTracks.get(0), availableTracks);
        roundDialog.setTitle("Rondsnijder");
        roundDialog.setHeaderText("Dobbelsteen wissel met RoundTrack");
        roundDialog.setContentText("Kies ronde dobbelsteen:");

        Optional<Integer> roundResult = roundDialog.showAndWait();
        int chosenRound = roundResult.orElse(0);

        return new Object[]{chosenDraftDie, chosenRound};
    }
}
