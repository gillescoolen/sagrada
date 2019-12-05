package sagrada.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class RoundTrack {
    private int current;
    private Map<Integer, Die> track = new LinkedHashMap<>() {
        {
            put(1, null);
            put(2, null);
            put(3, null);
            put(4, null);
            put(5, null);
            put(6, null);
            put(7, null);
            put(8, null);
            put(9, null);
            put(10, null);
        }
    };

    public void next(Die die) {
        this.track.put(this.current + 1, die);
    }

    public void updateTrack(int round, Die die) {
        this.track.replace(round, die);
    }

    public Die getDieByKey(int key) {
        return this.track.get(key);
    }

    public Map<Integer, Die> getTrack() {
        return Map.copyOf(this.track);
    }

    public int getCurrent() {
        return this.current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
