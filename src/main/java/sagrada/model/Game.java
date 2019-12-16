package sagrada.model;

import sagrada.util.Observable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Game extends Observable<Game> {
    private int id;
    private Die selectedDie;
    private Player playerTurn;
    private LocalDateTime createdOn;
    private final RoundTrack roundTrack;
    private final DraftPool draftPool;
    private final List<Player> players = new ArrayList<>(2);

    private final List<ToolCard> toolCards = new ArrayList<>(3);
    private final List<PublicObjectiveCard> objectiveCards = new ArrayList<>(3);

    private final List<FavorToken> favorTokens = new ArrayList<>(24);


    public Game() {
        this.draftPool = new DraftPool();
        this.roundTrack = new RoundTrack();
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
        this.update(this);
    }

    public Player getPlayerTurn() {
        return this.playerTurn;
    }

    public void setPlayerTurn(Player playerTurn) {
        this.playerTurn = playerTurn;
        this.update(this);
    }

    public LocalDateTime getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
        this.update(this);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        this.update(this);
    }

    public void addPlayers(List<Player> players) {
        this.players.clear();
        this.players.addAll(players);
        this.update(this);
    }

    public void addToolCard(ToolCard toolCard) {
        this.toolCards.add(toolCard);
        this.update(this);
    }

    public void addToolCard(List<ToolCard> toolCards) {
        this.toolCards.addAll(toolCards);
        this.update(this);
    }

    public void addObjectiveCard(PublicObjectiveCard objectiveCard) {
        this.objectiveCards.add(objectiveCard);
        this.update(this);
    }

    public void addObjectiveCard(List<PublicObjectiveCard> objectiveCards) {
        this.objectiveCards.addAll(objectiveCards);
        this.update(this);
    }

    public void addFavorToken(FavorToken favorToken) {
        this.favorTokens.add(favorToken);
        this.update(this);
    }

    public void updateDraftPool(Die oldDie, Die newDie) {
        this.draftPool.updateDraft(oldDie, newDie);
        this.update(this);
    }

    public void addFavorTokens(List<FavorToken> favorTokens) {
        this.favorTokens.addAll(favorTokens);
    }

    public List<Player> getPlayers() {
        return List.copyOf(this.players);
    }

    public List<ToolCard> getToolCards() {
        return List.copyOf(this.toolCards);
    }

    public List<PublicObjectiveCard> getObjectiveCards() {
        return List.copyOf(this.objectiveCards);
    }

    public List<FavorToken> getFavorTokens() {
        return List.copyOf(this.favorTokens);
    }

    public void removeFavorToken(FavorToken favorToken) {
        this.favorTokens.remove(favorToken);
        this.update(this);
    }

    public void removeFavorTokens(List<FavorToken> favorTokens) {
        this.favorTokens.removeAll(favorTokens);
    }

    public DraftPool getDraftPool() {
        return this.draftPool;
    }

    public void removeDieFromDraftPool(Die die) {
        this.draftPool.removeDice(die);
        this.update(this);
    }

    public void addDiceInDraftPool(List<Die> dieList) {
        this.draftPool.addAllDice(dieList);
        this.update(this);
    }

    public void throwDice() {
        this.draftPool.throwDice();
        this.update(this);
    }

    public Player getOwner() {
        return this.players
                .stream()
                .filter(player -> player.getPlayStatus() == PlayStatus.CHALLENGER)
                .findFirst()
                .orElse(null);
    }

    public Player getPlayerByName(String name) {
        return this.players
                .stream()
                .filter(player -> player.getAccount().getUsername().equals(name))
                .findFirst()
                .orElse(null);
    }

    public RoundTrack getRoundTrack() {
        return this.roundTrack;
    }

    public void setRoundTrack(RoundTrack roundTrack) {
        this.roundTrack.setCurrent(roundTrack.getCurrent());
        this.roundTrack.setTrack(roundTrack.getTrack());
        this.update(this);
    }

    public int getDiceCount() {
        return (this.players.size() * 2) + 1;
    }

    public void createPlayers() {
        // TODO: implement
    }

    public void play() {
        // TODO: implement
    }

    // FIXME: rename function, init functions are bad practice
    public void init() {
        // TODO: implement
    }

    public Die getSelectedDie() {
        return selectedDie;
    }

    public void setSelectedDie(Die selectedDie) {
        this.selectedDie = selectedDie;
        this.update(this);
    }
}
