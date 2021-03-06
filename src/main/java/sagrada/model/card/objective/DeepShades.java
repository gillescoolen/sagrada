package sagrada.model.card.objective;

import sagrada.model.PatternCard;
import sagrada.model.PublicObjectiveCard;

public final class DeepShades extends PublicObjectiveCard {
    public DeepShades(int id, String name, String description, int points) {
        super(id, name, description, points);
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        return this.calculateSets(patternCard, 5, 6) * this.getPoints();
    }
}