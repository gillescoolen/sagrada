package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;

import java.util.HashSet;

public final class ColumnShadeVariety extends PublicObjectiveCard {
    public ColumnShadeVariety(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        var amountOfPoints = 0;

        for (var xPosition = 1; xPosition < 5; ++xPosition) {
            var columnValueSet = new HashSet<Integer>();

            for (var yPosition = 1; yPosition < 4; ++yPosition) {
                var square = patternCard.getSquareByXAndY(xPosition, yPosition);

                if (square.getDie() != null) {
                    columnValueSet.add(square.getDie().getValue());
                }
            }

            if (columnValueSet.size() == 4) {
                amountOfPoints += 4;
            }
        }

        return amountOfPoints;
    }
}
