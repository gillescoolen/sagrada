package sagrada.model;

import java.util.Objects;

public abstract class ObjectiveCard extends Card {
    public ObjectiveCard(String name) {
        super(name);
    }

    public abstract int calculatePoints(PatternCard patternCard);

    protected int calculateSets(PatternCard patternCard, int first, int second) {
        int firstNumber = (int) patternCard.getSquares().stream().filter(square -> Objects.nonNull(square.getDie()))
                .filter(square -> square.getDie().getValue() == first).count();
        int secondNumber = (int) patternCard.getSquares().stream().filter(square -> Objects.nonNull(square.getDie()))
                .filter(square -> square.getDie().getValue() == second).count();

        return Math.min(firstNumber, secondNumber);
    }
}
