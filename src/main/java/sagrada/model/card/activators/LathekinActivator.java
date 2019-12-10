package sagrada.model.card.activators;

import javafx.scene.control.ChoiceDialog;
import sagrada.controller.GameController;
import sagrada.model.Game;
import sagrada.model.Player;
import sagrada.model.Square;
import sagrada.model.ToolCard;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public final class LathekinActivator extends ToolCardActivator {
    private Player player;
    private final static int SQUARES_TO_MOVE = 2;

    public LathekinActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public void activate() throws SQLException {
        this.player = this.controller.getPlayer();
        Game game = this.controller.getGame();

        Square[] squares = this.askWhichDiceToMove();
        Square[] newSquares = this.askNewSquares(squares);

        List<Square[]> message = new ArrayList<>(SQUARES_TO_MOVE);
        message.add(squares);
        message.add(newSquares);

        this.toolCard.use(game.getDraftPool(), this.player.getDiceBag(), this.player.getPatternCard(), game.getRoundTrack(), player, game, message);
    }

    private Square[] askNewSquares(Square[] squares) {
        Square[] squaresToMove = new Square[SQUARES_TO_MOVE];
        Set<Square> newSquares = new HashSet<>(SQUARES_TO_MOVE);

        for (Square square : squares) {
            List<Square> filteredSquares = this.player.getPatternCard().getSquares().stream()
                    .filter(oldSquare -> oldSquare.getDie() == null)
                    .filter(oldSquare -> oldSquare.getColor() == square.getColor()).collect(Collectors.toList());

            newSquares.addAll(filteredSquares);
        }

        List<Square> cleanList = new ArrayList<>(newSquares);


        for (int i = 0; i < squaresToMove.length; i++) {
            ChoiceDialog<Square> dialog = new ChoiceDialog<>(cleanList.get(0), cleanList);
            dialog.setTitle("Loodopenhaler 2/2");
            dialog.setHeaderText("Dobbelsteen keuze");
            dialog.setContentText("Kies dobbelsteen:");

            Optional<Square> result = dialog.showAndWait();

            if (result.isEmpty()) {
                i = i - 1; // TODO: test if this works.
            } else {
                squaresToMove[i] = result.get();
            }

        }

        return squaresToMove;
    }


    private Square[] askWhichDiceToMove() {
        Square[] squaresToMove = new Square[SQUARES_TO_MOVE];

        for (int i = 0; i < squaresToMove.length; i++) {
            ChoiceDialog<Square> dialog = new ChoiceDialog<>(this.player.getPatternCard().getSquares().get(0), this.player.getPatternCard().getSquares());
            dialog.setTitle("Loodopenhaler 1/2");
            dialog.setHeaderText("Dobbelsteen keuze");
            dialog.setContentText("Kies dobbelsteen:");

            Optional<Square> result = dialog.showAndWait();

            if (result.isEmpty()) {
                i = i - 1; // TODO: test if this works.
            } else {
                squaresToMove[i] = result.get();
            }

        }

        return squaresToMove;
    }
}
