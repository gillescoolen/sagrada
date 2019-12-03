package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ShadeVariety extends PublicObjectiveCard {
    public ShadeVariety(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        List<Integer> totals = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            int number = i;
            totals.add((int) patternCard.getSquares().stream().filter(square -> square.getDie().getValue() == number).count());
        }

        return Collections.min(totals) * this.getPoints();
    }
}
