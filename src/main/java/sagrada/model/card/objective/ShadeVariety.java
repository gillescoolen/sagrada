package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public final class ShadeVariety extends PublicObjectiveCard {
    public ShadeVariety(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        var totals = new ArrayList<Integer>();

        for (int i = 1; i <= 6; i++) {
            var number = i;
            totals.add((int) patternCard.getSquares().stream().filter(square -> Objects.nonNull(square.getDie()))
                    .filter(square -> square.getDie().getValue() == number).count());
        }

        return Collections.min(totals) * this.getPoints();
    }
}
