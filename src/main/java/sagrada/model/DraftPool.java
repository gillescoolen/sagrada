package sagrada.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DraftPool {
    private List<Die> dice = new ArrayList<>();

    public List<Die> getDice() {
        return this.dice;
    }

    public void addDice(Die die) {
        this.dice.add(die);
    }

    public void removeDice(Die die) {
        this.dice.remove(die);
    }

    public void addAllDice(List<Die> dice) {
        this.dice.addAll(dice);
    }

    public void removeAllDice() {
        this.dice.clear();
    }

    public void updateDraft(Die oldDieIndex, Die newDie) {
        this.dice.set(dice.indexOf(oldDieIndex), newDie);
    }

    public void reRollDraft() {
        Random random = new Random();
        this.dice.forEach(die -> die.setValue(random.nextInt(6) + 1));
    }
}
