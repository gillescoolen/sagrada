package sagrada.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private int id;
    private Account account;
    private Game game;
    private PlayStatus playStatus;
    private int sequenceNumber; // seqnr
    private boolean isCurrentPlayer = false;
    private Color privateObjectiveCardColor;
    private PatternCard patternCard;
    private final List<PatternCard> cardOptions;
    private final List<FavorToken> favorTokens;
    private Integer score = 0;

    /**
     * @param id the id of a player
     * @param account the account of a player
     * @param game the game the player is in
     * @param playStatus the status of the player
     * @param isCurrentPlayer whether it is the player's turn
     * @param privateObjectiveCardColor the color of the private objective card
     */
    public Player(int id, Account account, Game game, PlayStatus playStatus, boolean isCurrentPlayer, Color privateObjectiveCardColor) {
        this.cardOptions = new ArrayList<>();
        this.favorTokens = new ArrayList<>();
        this.id = id;
        this.account = account;
        this.game = game;
        this.playStatus = playStatus;
        this.isCurrentPlayer = isCurrentPlayer;
        this.privateObjectiveCardColor = privateObjectiveCardColor;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Account getAccount() {
        return this.account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean isCurrentPlayer() {
        return this.isCurrentPlayer;
    }

    public void setCurrentPlayer(boolean currentPlayer) {
        this.isCurrentPlayer = currentPlayer;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public PlayStatus getPlayStatus() {
        return this.playStatus;
    }

    public List<PatternCard> getCardOptions() {
        return List.copyOf(this.cardOptions);
    }

    public List<FavorToken> getFavorTokens() {
        return List.copyOf(this.favorTokens);
    }

    public void setPlayStatus(PlayStatus playStatus) {
        this.playStatus = playStatus;
    }

    public Game getGame() {
        return this.game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Color getPrivateObjectiveCardColor() {
        return this.privateObjectiveCardColor;
    }

    public void setPrivateObjectiveCardColor(Color privateObjectiveCardColor) {
        this.privateObjectiveCardColor = privateObjectiveCardColor;
    }

    public void addCardOption(PatternCard patternCard) {
        this.cardOptions.add(patternCard);
    }

    public void addFavorToken(FavorToken favorToken) {
        this.favorTokens.add(favorToken);
    }

    public void removeFavorToken(FavorToken favorToken) {
        this.favorTokens.remove(favorToken);
    }

    public PatternCard getPatternCard() {
        return this.patternCard;
    }

    public void setPatternCard(PatternCard patternCard) {
        this.patternCard = patternCard;
    }
}
