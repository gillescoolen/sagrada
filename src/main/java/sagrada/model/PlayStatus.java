package sagrada.model;

public enum PlayStatus {
    CHALLENGER("challenger"),
    INVITED("challengee"),
    ACCEPTED("accepted"),
    DECLINED("refused"),
    DONE_PLAYING("finished"),
    CANCELLED("aborted");

    private final String playState;

    PlayStatus(String playState) {
        this.playState = playState;
    }

    public String getPlayState() {
        return this.playState;
    }
}
