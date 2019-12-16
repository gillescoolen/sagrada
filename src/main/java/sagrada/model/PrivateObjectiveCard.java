package sagrada.model;

public class PrivateObjectiveCard extends ObjectiveCard {
    private final Color color;

    public PrivateObjectiveCard(Color color) {
        super("Shades of " + color);
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public int calculatePoints(PatternCard patternCard) {
        return patternCard
                .getSquares()
                .stream()
                .filter(square -> square.getDie() != null)
                .filter(square -> square.getColor() == this.color)
                .mapToInt(square -> square.getDie().getValue())
                .sum();
    }
}
