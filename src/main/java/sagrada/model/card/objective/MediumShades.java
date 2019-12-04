package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;

public final class MediumShades extends PublicObjectiveCard {
    public MediumShades(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        return this.calculateSets(patternCard, 3, 4) * this.getPoints();
    }
}