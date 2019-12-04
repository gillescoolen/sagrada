package sagrada.model;

import java.util.ArrayList;
import java.util.List;

public class DraftPool {
    private List<Die> dice = new ArrayList<>();

    public List<Die> getDice() {
        return this.dice;
    }

    public void addDice(Die die) {
        this.dice.add(die);
    }

    public void addAllDice(List<Die> dice) {
        this.dice.addAll(dice);
    }

    public void updateDraft(Die oldDieIndex, Die newDie) {
        this.dice.set(dice.indexOf(oldDieIndex), newDie);
    }
}
