package sagrada.model;

public enum PlayStatus {
    CHALLENGER("uitdager"),
    INVITED("uitgedaagde"),
    ACCEPTED("geaccepteerd"),
    DECLINED("geweigerd"),
    DONE_PLAYING("klaar"),
    CANCELLED("afgesloten");

    private final String playState;

    PlayStatus(String playState) {
        this.playState = playState;
    }

    public String getPlayState() {
        return this.playState;
    }
}
