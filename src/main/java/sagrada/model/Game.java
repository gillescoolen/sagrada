package sagrada.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game {
    private int id;
    private Player playerTurn;
    private Date createdOn;
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

    public Date getCreatedOn() { return this.createdOn; }

    public void setCreatedOn(Date createdOn) { this.createdOn = createdOn; }

    public void addPlayer(Player player) {
        this.players.add(player);
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
