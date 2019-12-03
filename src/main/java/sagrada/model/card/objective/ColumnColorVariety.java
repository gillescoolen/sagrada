package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;

import java.util.HashSet;

public final class ColumnColorVariety extends PublicObjectiveCard {
    public ColumnColorVariety(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        var amountOfPoints = 0;

        for (var xPosition = 1; xPosition < 5; ++xPosition) {
            var rowColorSet = new HashSet<String>();

            for (var yPosition = 1; yPosition < 4; ++yPosition) {
                var square = patternCard.getSquareByXAndY(xPosition, yPosition);

                if (square.getColor() != null) {
                    rowColorSet.add(square.getColor().getColor());
                }
            }

            if (rowColorSet.size() == 4) {
                amountOfPoints += 5;
            }
        }

        return amountOfPoints;
    }
}
