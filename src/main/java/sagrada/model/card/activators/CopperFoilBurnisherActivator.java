package sagrada.model.card.activators;

import javafx.scene.control.ChoiceDialog;
import sagrada.controller.GameController;
import sagrada.model.Game;
import sagrada.model.Player;
import sagrada.model.Square;
import sagrada.model.ToolCard;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CopperFoilBurnisherActivator extends ToolCardActivator {
    private Player player;

    public CopperFoilBurnisherActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public boolean activate() throws SQLException {
        this.player = this.controller.getPlayer();
        Game game = this.controller.getGame();

        Square square = this.askWhichDiceShouldBeMoved();

        if (square == null) return false;

        Square newSquare = this.askNewPosition(square);

        if (newSquare == null) return false;

        Object[] message = new Object[2];
        message[0] = square;
        message[1] = newSquare;

        return this.toolCard.use(game.getDraftPool(), this.player.getDiceBag(), this.player.getPlayerFrame(), game.getRoundTrack(), this.player, game, message);
    }

    private Square askWhichDiceShouldBeMoved() {
        List<Square> squaresWithDie = this.player.getPlayerFrame().getSquares().stream()
                .filter(square -> square.getDie() != null)
                .collect(Collectors.toList());

        if (squaresWithDie.size() == 0) return null;

        ChoiceDialog<Square> dialog = new ChoiceDialog<>(squaresWithDie.get(0), squaresWithDie);
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
        List<Square> emptyPositions = this.player.getPlayerFrame().getSquares().stream()
                .filter(square -> square.getDie() == null)
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
