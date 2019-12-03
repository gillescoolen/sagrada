package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;

import java.util.HashSet;

public final class RowColorVariety extends PublicObjectiveCard {
    public RowColorVariety(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        var amountOfPoints = 0;

        for (var yPosition = 1; yPosition < 4; ++yPosition) {
            var rowColorSet = new HashSet<String>();

            for (var xPosition = 1; xPosition < 5; ++xPosition) {
                var square = patternCard.getSquareByXAndY(xPosition, yPosition);

                if (square.getColor() != null) {
                    rowColorSet.add(square.getColor().getColor());
                }
            }

            if (rowColorSet.size() == 5) {
                amountOfPoints += 6;
            }
        }

        return amountOfPoints;
    }
}
