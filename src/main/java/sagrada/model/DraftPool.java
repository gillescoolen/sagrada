package sagrada.model;

import sagrada.util.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DraftPool extends Observable<DraftPool> {
    private List<Die> dice = new ArrayList<>();

    public List<Die> getDice() {
        return List.copyOf(this.dice);
    }

    public void addDice(Die die) {
        this.dice.add(die);
        this.update(this);
    }

    public void removeDice(Die die) {
        this.dice.remove(die);
        this.update(this);
    }

    public void addAllDice(List<Die> dice) {
        this.dice.clear();
        this.dice.addAll(dice);
        this.update(this);
    }

    public void removeAllDice() {
        this.dice.clear();
        this.update(this);
    }

    public void updateDraft(Die oldDieIndex, Die newDie) {
        this.dice.set(dice.indexOf(oldDieIndex), newDie);
        this.update(this);
    }

    public void throwDice() {
        Random random = new Random();
        this.dice.forEach(die -> die.setValue(random.nextInt(6) + 1));
        this.update(this);
    }
}
