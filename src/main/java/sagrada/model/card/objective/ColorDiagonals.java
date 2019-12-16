package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.Position;
import sagrada.model.PublicObjectiveCard;
import sagrada.model.Square;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class ColorDiagonals extends PublicObjectiveCard {
    public ColorDiagonals(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        var squareSet = new HashSet<Square>();

        for (var square : patternCard.getSquares()) {
            var squareList = this.getSquareListBySquare(patternCard, square);

            for (var possibleSquare : squareList) {
                if (possibleSquare != null && possibleSquare.getDie() != null && square.getDie() != null) {
                    var added = false;

                    if (possibleSquare.getDie().getColor().equals(square.getDie().getColor())) {
                        squareSet.add(square);
                        added = true;
                    }

                    if (!added) {
                        var possibleTopSquares = this.getSquareListByPosition(patternCard, new Position(square.getPosition().getX(), square.getPosition().getY()));

                        for (var possibleTopSquare : possibleTopSquares) {
                            if (possibleTopSquare != null && possibleTopSquare.getDie() != null && square.getDie() != null) {
                                if (possibleTopSquare.getDie().getColor().equals(square.getDie().getColor())) {
                                    squareSet.add(square);
                                }
                            }
                        }
                    }
                }
            }
        }

        return squareSet.size();
    }

    private List<Square> getSquareListByPosition(PatternCard patternCard, Position position) {
        var squareList = new ArrayList<Square>();

        var topLeft = patternCard.getSquareByXAndY(position.getX() - 1, position.getY() - 1);
        var topRight = patternCard.getSquareByXAndY(position.getX() + 1, position.getY() - 1);

        squareList.add(topLeft);
        squareList.add(topRight);

        return squareList;
    }

    private List<Square> getSquareListBySquare(PatternCard patternCard, Square square) {
        var currentPosition = square.getPosition();
        var squareList = new ArrayList<Square>();

        var bottomLeft = patternCard.getSquareByXAndY(currentPosition.getX() - 1, currentPosition.getY() + 1);
        var bottomRight = patternCard.getSquareByXAndY(currentPosition.getX() + 1, currentPosition.getY() + 1);

        squareList.add(bottomLeft);
        squareList.add(bottomRight);

        return squareList;
    }
}
