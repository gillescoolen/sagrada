package sagrada.model;

public abstract class ObjectiveCard extends Card {
    public ObjectiveCard(String name) {
        super(name);
    }

    public abstract int calculatePoints(PatternCard patternCard);
}
