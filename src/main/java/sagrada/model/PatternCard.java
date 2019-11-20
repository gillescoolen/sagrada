package sagrada.model;

public class PatternCard {
    private int id;
    private String name;
    private int difficulty;
    private int standard;

    public PatternCard(int id, String name, int difficulty, int standard) {
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

    public int getStandard() {
        return this.standard;
    }
}
