package sagrada.model;

import sagrada.database.repositories.GameRepository;
import sagrada.database.repositories.PlayerRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private int id;
    private Account account;
    private PlayStatus playStatus;
    private int sequenceNumber; // seqnr
    private boolean isCurrentPlayer = false;
    private PrivateObjectiveCard privateObjectiveCard;
    private PatternCard patternCard;
    private List<PatternCard> cardOptions = new ArrayList<>();
    private List<FavorToken> favorTokens = new ArrayList<>();
    private Integer score = 0;
    private DiceBag diceBag;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public void setPrivateObjectiveCard(PrivateObjectiveCard privateObjectiveCard) {
        this.privateObjectiveCard = privateObjectiveCard;
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

    public void setDiceBag(DiceBag diceBag) {
        this.diceBag = diceBag;
    }

    public void inviteOtherPlayer(String playerName, PlayerRepository playerRepository, Game game) throws SQLException {
        playerRepository.inviteOtherPlayer(playerName, game);
    }
}
