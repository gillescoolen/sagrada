package sagrada.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiceBag {
    private final List<Die> dice;

    public DiceBag(List<Die> dice) {
        this.dice = dice;
    }

    public List<Die> getRandomDice(int amount) {
        Collections.shuffle(this.dice);

        List<Die> randomDice = new ArrayList<>(this.dice.subList(0, amount));

        this.dice.removeAll(randomDice);

        return randomDice;
    }

    public void put(Die die) {
        this.dice.add(die);
    }
}
