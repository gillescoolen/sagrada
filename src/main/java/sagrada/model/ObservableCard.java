package sagrada.model;

import sagrada.util.Observable;

public abstract class ObservableCard<T> extends Observable<T> {
    private final String name;

    public String getName() {
        return this.name;
    }

    public ObservableCard(String name) {
        this.name = name;
    }
}
