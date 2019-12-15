package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;

import java.util.HashSet;

public final class RowShadeVariety extends PublicObjectiveCard {
    public RowShadeVariety(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        var amountOfPoints = 0;

        for (var yPosition = 1; yPosition <= 4; ++yPosition) {
            var rowValueSet = new HashSet<Integer>();

            for (var xPosition = 1; xPosition <= 5; ++xPosition) {
                var square = patternCard.getSquareByXAndY(xPosition, yPosition);

                if (square.getDie() != null) {
                    rowValueSet.add(square.getDie().getValue());
                }
            }

            if (rowValueSet.size() == 5) {
                amountOfPoints += this.getPoints();
            }
        }

        return amountOfPoints;
    }
}
