package sagrada.model.card.activators;

import javafx.scene.control.ChoiceDialog;
import sagrada.controller.GameController;
import sagrada.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CopperFoilBurnisherActivator extends ToolCardActivator {
    private Player player;

    public CopperFoilBurnisherActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public void activate() {
        this.player = this.controller.getPlayer();
        Game game = this.controller.getGame();


        Square square = this.askWhichDiceShouldBeMoved();
        Square newSquare = this.askNewPosition(square);

        Object[] message = new Object[2];
        message[0] = square;
        message[1] = newSquare;

        this.toolCard.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), message);
    }

    private Square askWhichDiceShouldBeMoved() {
        ChoiceDialog<Square> dialog = new ChoiceDialog<>(this.player.getPatternCard().getSquares().get(0), this.player.getPatternCard().getSquares());
        dialog.setTitle("Folie-aandrukker 1/2");
        dialog.setHeaderText("Dobbelsteen keuze");
        dialog.setContentText("Kies dobbelsteen:");

        Optional<Square> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return this.askWhichDiceShouldBeMoved();
        }

        return result.orElse(null);
    }

    private Square askNewPosition(final Square chosenSquare) {
        List<Square> emptyPositions = this.player.getPatternCard().getSquares().stream()
                .filter(square -> square.getDie() == null)
                .filter(square -> square.getColor() == chosenSquare.getColor())
                .collect(Collectors.toList());

        ChoiceDialog<Square> dialog = new ChoiceDialog<>(emptyPositions.get(0), emptyPositions);
        dialog.setTitle("Folie-aandrukker 2/2");
        dialog.setHeaderText("Positie keuze");
        dialog.setContentText("Kies een nieuwe positie:");

        Optional<Square> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return this.askNewPosition(chosenSquare);
        }

        return result.orElse(null);
    }
}
