package sagrada.model;

import java.util.ArrayList;
import java.util.List;

public class PatternCard extends Card {
    private final int id;
    private final int difficulty;
    private final int standard;

    private static final int AMOUNT_OF_SQUARES = 20;

    private final List<Square> squares = new ArrayList<>(AMOUNT_OF_SQUARES);

    public PatternCard(int id, String name, int difficulty, int standard) {
        super(name);
        this.id = id;
        this.difficulty = difficulty;
        this.standard = standard;
    }

    public int getId() {
        return this.id;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public int getStandard() {
        return this.standard;
    }

    public Square getSquareByXAndY(int x, int y) {
        for (var square : this.squares) {
            var position = square.getPosition();

            if (position.getX() == x && position.getY() == y) {
                return square;
            }
        }

        return null;
    }

    public List<Square> getSquares() {
        return List.copyOf(this.squares);
    }

    public void placeDie() {
        // TODO: implement this function
    }
}
