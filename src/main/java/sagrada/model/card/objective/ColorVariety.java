package sagrada.model.card.objective;

import sagrada.model.Color;
import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;

import java.util.ArrayList;
import java.util.Collections;

public final class ColorVariety extends PublicObjectiveCard {
    public ColorVariety(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        var totals = new ArrayList<Integer>();

        for (Color color : Color.values()) {
            totals.add((int) patternCard.getSquares().stream().filter(square -> square.getDie().getColor() == color).count());
        }

        return Collections.min(totals) * this.getPoints();
    }
}

