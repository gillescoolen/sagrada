package sagrada.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class RoundTrack {
    private int current;
    private Map<Integer, Die> track = new LinkedHashMap<>();

    public RoundTrack() {
        this.track.put(1, null);
        this.track.put(2, null);
        this.track.put(3, null);
        this.track.put(4, null);
        this.track.put(5, null);
        this.track.put(6, null);
        this.track.put(7, null);
        this.track.put(8, null);
        this.track.put(9, null);
        this.track.put(10, null);
    }

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
