package sagrada.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {
    private int id;
    private Player playerTurn;
    private LocalDateTime createdOn;
    private final List<Player> players = new ArrayList<>(2);

    private final List<ToolCard> toolCards = new ArrayList<>(3);
    private final List<PublicObjectiveCard> objectiveCards = new ArrayList<>(3);

    private final List<FavorToken> favorTokens = new ArrayList<>(24);

    private final List<Die> dice = new ArrayList<>(90);

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Player getPlayerTurn() {
        return this.playerTurn;
    }

    public void setPlayerTurn(Player playerTurn) {
        this.playerTurn = playerTurn;
    }

    public LocalDateTime getCreatedOn() { return this.createdOn; }

    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void addPlayers(List<Player> players) {
        this.players.addAll(players);
    }

    public void addToolCard(ToolCard toolCard) {
        this.toolCards.add(toolCard);
    }

    public void addObjectiveCard(PublicObjectiveCard objectiveCard) {
        this.objectiveCards.add(objectiveCard);
    }

    public void addFavorToken(FavorToken favorToken) {
        this.favorTokens.add(favorToken);
    }

    public void addDie(Die die) {
        this.dice.add(die);
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

    public List<Die> getDice() {
        return List.copyOf(this.dice);
    }

    public void removeDie(Die die) {
        this.dice.remove(die);
    }

    public void removeFavorToken(FavorToken favorToken) {
        this.favorTokens.remove(favorToken);
    }

    public Player getOwner() {
        var filteredPlayers = this.players.stream().filter(player -> player.getPlayStatus() == PlayStatus.CHALLENGER).collect(Collectors.toList());

        if (filteredPlayers.size() > 0) {
            return filteredPlayers.get(0);
        } return null;
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
}
