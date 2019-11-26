package sagrada.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final int id;
    private final Account account;
    private PlayStatus playStatus;
    private int sequenceNumber; // seqnr
    private boolean isCurrentPlayer = false;
    private final PrivateObjectiveCard privateObjectiveCard;
    private PatternCard patternCard;
    private final List<PatternCard> cardOptions = new ArrayList<>();
    private final List<FavorToken> favorTokens = new ArrayList<>();
    private Integer score = 0;
    private DiceBag diceBag = new DiceBag(new ArrayList<>());

    /**
     * @param id                        the id of a player
     * @param account                   the account of a player
     * @param playStatus                the status of the player
     * @param isCurrentPlayer           whether it is the player's turn
     * @param privateObjectiveCardColor the color of the private objective card
     */
    public Player(int id, Account account, PlayStatus playStatus, boolean isCurrentPlayer, Color privateObjectiveCardColor, DiceBag diceBag) {
        this.id = id;
        this.account = account;
        this.playStatus = playStatus;
        this.isCurrentPlayer = isCurrentPlayer;
        this.privateObjectiveCard = new PrivateObjectiveCard(privateObjectiveCardColor);
        this.diceBag = diceBag;
    }

    /**
     * @param id                        the id of a player
     * @param account                   the account of a player
     * @param playStatus                the status of the player
     */
    public Player(int id, Account account, PlayStatus playStatus, boolean isCurrentPlayer, PrivateObjectiveCard privateObjectiveCard) {
        this.id = id;
        this.account = account;
        this.playStatus = playStatus;
        this.isCurrentPlayer = isCurrentPlayer;
        this.privateObjectiveCard = privateObjectiveCard;
    }

    public int getId() {
        return this.id;
    }

    public Account getAccount() {
        return this.account;
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

    public PrivateObjectiveCard getPrivateObjectiveCard() {
        return this.privateObjectiveCard;
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

    public List<Die> grabRandomDice(int amount) {
        return this.diceBag.getRandomDice(amount);
    }
}
