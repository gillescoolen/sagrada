package sagrada.model;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.PlayerFrameRepository;
import sagrada.database.repositories.PlayerRepository;
import sagrada.database.repositories.ToolCardRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private int id;
    private Account account;
    private PlayStatus playStatus;
    private Integer sequenceNumber; // seqnr
    private boolean isCurrentPlayer = false;
    private PrivateObjectiveCard privateObjectiveCard;
    private PatternCard patternCard;
    private PatternCard playerFrame;
    private List<PatternCard> cardOptions = new ArrayList<>();
    private List<FavorToken> favorTokens = new ArrayList<>();
    private Integer score = 0;
    private DiceBag diceBag;
    private boolean invalidFrameField = false;
    private Object message;

    public PatternCard getPlayerFrame() {
        return this.playerFrame;
    }

    public void setPlayerFrame(PatternCard playerFrame) {
        this.playerFrame = playerFrame;
    }

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

    public Integer getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
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

    public void addCardOption(List<PatternCard> patternCards) {
        this.cardOptions.addAll(patternCards);
    }

    public void addFavorToken(FavorToken favorToken) {
        this.favorTokens.add(favorToken);
    }

    public void addFavorTokens(List<FavorToken> favorTokens) {
        this.favorTokens.addAll(favorTokens);
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

    public List<Die> grabFromDiceBag(int amount) {
        return this.diceBag.getRandomDice(amount);
    }

    public DiceBag getDiceBag() {
        return this.diceBag;
    }

    public void setDiceBag(DiceBag diceBag) {
        this.diceBag = diceBag;
    }

    public Object getMessage() {
        return this.message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public boolean hasInvalidFrameField() {
        return this.invalidFrameField;
    }

    public void setInvalidFrameField(boolean invalidFrameField) {
        this.invalidFrameField = invalidFrameField;
    }

    public void skipTurn(PlayerRepository playerRepository, Game game) throws SQLException {
        this.isCurrentPlayer = false;
        playerRepository.nextPlayerTurn(this, game);
    }

    public void validateFrameField(DatabaseConnection connection, Game game) {
        var playerFrameRepository = new PlayerFrameRepository(connection);

        for (var i = 0; i < 20; ++i) {
            var patternCardSquares = this.patternCard.getSquares();
            var frameFieldSquares = this.playerFrame.getSquares();
            var frameFieldSquare = frameFieldSquares.get(i);
            var patternCardSquare = patternCardSquares.get(i);

            if (frameFieldSquare.getDie() != null) {

                boolean colorNotCorrect = false;
                boolean valueNotCorrect = false;

                if (patternCardSquare.getColor() != null) {
                    colorNotCorrect = !patternCardSquare.getColor().equals(frameFieldSquare.getDie().getColor());
                }

                if (patternCardSquare.getValue() != null) {
                    valueNotCorrect = !patternCardSquare.getValue().equals(frameFieldSquare.getDie().getValue());
                    if (patternCardSquare.getValue() == 0) {
                        // This is the check if a square have value 0,
                        // that means you can paste ever die in this square,
                        // and also the color check if upside
                        valueNotCorrect = false;
                    }
                }

                if (colorNotCorrect || valueNotCorrect) {
                    if (this.toolCardNotUsed(connection, game, frameFieldSquare.getDie())) {
                        var frameSquare = frameFieldSquares.get(i);

                        frameSquare.setDie(null);
                        frameSquare.setValue(0);
                        frameSquare.setColor(null);

                        try {
                            playerFrameRepository.resetSquare(this, frameSquare, game);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        this.playerFrame.setSquares(frameFieldSquares);
                    }
                }
            }
        }

        for (var i = 0; i < 20; ++i) {
            var patternCardSquares = this.patternCard.getSquares();
            var frameFieldSquares = this.playerFrame.getSquares();
            var patternCardSquare = patternCardSquares.get(i);
            var frameFieldSquare = frameFieldSquares.get(i);

            var frameSquares = new ArrayList<Square>();

            frameSquares.add(this.playerFrame.getSquareByXAndY(frameFieldSquare.getPosition().getX() - 1, frameFieldSquare.getPosition().getY()));
            frameSquares.add(this.playerFrame.getSquareByXAndY(frameFieldSquare.getPosition().getX(), frameFieldSquare.getPosition().getY() - 1));
            frameSquares.add(this.patternCard.getSquareByXAndY(patternCardSquare.getPosition().getX() - 1, patternCardSquare.getPosition().getY()));
            frameSquares.add(this.patternCard.getSquareByXAndY(patternCardSquare.getPosition().getX(), patternCardSquare.getPosition().getY() - 1));

            for (var square : frameSquares) {
                if (square != null && square.getDie() != null && frameFieldSquare.getDie() != null) {
                    if (square.getDie().getColor().equals(frameFieldSquare.getDie().getColor()) || square.getDie().getValue().equals(frameFieldSquare.getDie().getValue())) {
                        if (this.toolCardNotUsed(connection, game, square.getDie())) {
                            frameFieldSquare.setDie(null);
                            frameFieldSquare.setValue(0);
                            frameFieldSquare.setColor(null);

                            try {
                                playerFrameRepository.resetSquare(this, frameFieldSquare, game);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            this.playerFrame.setSquares(frameFieldSquares);
                        }
                    }
                }
            }
        }
    }

    private boolean toolCardNotUsed(DatabaseConnection connection, Game game, Die die) {
        var toolCardRepository = new ToolCardRepository(connection);

        try {
            return !toolCardRepository.isGameDieAffected(game.getId(), die);
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public FavorToken getNonAffectedFavorToken() {
        return this.favorTokens.stream().filter(favorToken -> favorToken.getToolCard() == null).findFirst().orElse(null);
    }
}
