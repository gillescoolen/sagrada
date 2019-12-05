package sagrada.model;

import sagrada.util.Observable;

public abstract class ObservableCard extends Observable<PatternCard> {
    private final String name;

    public String getName() {
        return this.name;
    }

    public ObservableCard(String name) {
        this.name = name;
    }
}
