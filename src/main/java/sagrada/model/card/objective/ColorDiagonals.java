package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.Position;
import sagrada.model.PublicObjectiveCard;
import sagrada.model.Square;

import java.util.ArrayList;
import java.util.List;

public final class ColorDiagonals extends PublicObjectiveCard {
    public ColorDiagonals(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        var points = 0;

        for (var square : patternCard.getSquares()) {
            if (square.getColor() != null) {
                var squareList = this.getSquareList(patternCard, square);

                for (var possibleSquare : squareList) {
                    if (possibleSquare.getDie() != null && square.getDie() != null) {
                        if (possibleSquare.getDie().getColor().equals(square.getDie().getColor())) {
                            ++points;
                        }
                    }
                }
            }
        }

        return points;
    }

    private List<Square> getSquareList(PatternCard patternCard, Square square) {
        var currentPosition = square.getPosition();
        var squareList = new ArrayList<Square>();

        var bottomLeft = patternCard.getSquareByXAndY(currentPosition.getX() - 1, currentPosition.getY() + 1);
        var bottomRight = patternCard.getSquareByXAndY(currentPosition.getX() + 1, currentPosition.getY() + 1);

        squareList.add(bottomLeft);
        squareList.add(bottomRight);

        return squareList;
    }
}
