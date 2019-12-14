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
        this.favorTokens.clear();
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

    public void invalidateField(PlayerFrameRepository repository) throws SQLException {
        repository.invalidateCard(this);
        this.setInvalidFrameField(true);
    }

    public void setCardAsValid(PlayerFrameRepository repository) throws SQLException {
        repository.setCardValid(this);
        this.setInvalidFrameField(false);
    }

    public boolean checkIfCardIsValid(PlayerFrameRepository repository) throws SQLException {
        boolean result = repository.checkIfCardIsValid(this);

        this.setInvalidFrameField(result);

        return result;
    }

    public void skipTurn(PlayerRepository playerRepository, Game game) throws SQLException {
        this.setCurrentPlayer(false);
        playerRepository.nextPlayerTurn(this, game);
    }

    public PatternCard validateFrameField(DatabaseConnection connection, Game game) {
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

        return this.playerFrame;
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

    public boolean getCurrent(PlayerRepository playerRepository) throws SQLException {
        var isCurrent = playerRepository.getIfCurrent(this.getId());
        this.isCurrentPlayer = isCurrent;
        return isCurrent;
    }

    // FIXME: refactor this garbage
    public Integer getNextSequenceNumber(int playerAmount, Player player) {
        int nextSequence = 0;

        if (playerAmount == 2) {
            switch (player.getSequenceNumber()) {
                case 1:
                    player.setSequenceNumber(4);
                    nextSequence = 2;
                    break;
                case 2:
                    player.setSequenceNumber(3);
                    nextSequence = 3;
                    break;
                case 3:
                    player.setSequenceNumber(1);
                    nextSequence = 4;
                    break;
                case 4:
                    player.setSequenceNumber(2);
                    nextSequence = 1;
                    break;
            }
        }

        if (playerAmount == 3) {
            switch (player.getSequenceNumber()) {
                case 1:
                    player.setSequenceNumber(6);
                    nextSequence = 2;
                    break;
                case 2:
                    player.setSequenceNumber(5);
                    nextSequence = 3;
                    break;
                case 3:
                    player.setSequenceNumber(4);
                    nextSequence = 4;
                    break;
                case 4:
                    player.setSequenceNumber(2);
                    nextSequence = 5;
                    break;
                case 5:
                    player.setSequenceNumber(1);
                    nextSequence = 6;
                    break;
                case 6:
                    player.setSequenceNumber(3);
                    nextSequence = 1;
                    break;
            }
        }

        if (playerAmount == 4) {
            switch (player.getSequenceNumber()) {
                case 1:
                    player.setSequenceNumber(8);
                    nextSequence = 2;
                    break;
                case 2:
                    player.setSequenceNumber(7);
                    nextSequence = 3;
                    break;
                case 3:
                    player.setSequenceNumber(6);
                    nextSequence = 4;
                    break;
                case 4:
                    player.setSequenceNumber(5);
                    nextSequence = 5;
                    break;
                case 5:
                    player.setSequenceNumber(3);
                    nextSequence = 6;
                    break;
                case 6:
                    player.setSequenceNumber(2);
                    nextSequence = 7;
                    break;
                case 7:
                    player.setSequenceNumber(1);
                    nextSequence = 8;
                    break;
                case 8:
                    player.setSequenceNumber(4);
                    nextSequence = 1;
                    break;
            }
        }

        return nextSequence;
    }
}
