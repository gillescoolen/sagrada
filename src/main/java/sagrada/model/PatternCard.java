package sagrada.model;

public class PatternCard {
    private final int id;
    private final String name;
    private final int difficulty;
    private final boolean standard;

    public PatternCard(int id, String name, int difficulty, boolean standard) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.standard = standard;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public boolean getStandard() {
        return this.standard;
    }
}
