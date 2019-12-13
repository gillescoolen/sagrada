package sagrada.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class RoundTrack {
    private int current;
    private Map<Integer, Die> track = new LinkedHashMap<>() {
        {
            put(1, new Die(null, null));
            put(2, new Die(null, null));
            put(3, new Die(null, null));
            put(4, new Die(null, null));
            put(5, new Die(null, null));
            put(6, new Die(null, null));
            put(7, new Die(null, null));
            put(8, new Die(null, null));
            put(9, new Die(null, null));
            put(10, new Die(null, null));
        }
    };

    public void updateTrack(int round, Die die) {
        this.track.replace(round, die);
    }

    public void putTrack(int round, Die die) {
        this.track.put(round, die);
    }

    public void setTrack(Map<Integer, Die> track) {
        this.track = track;
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
