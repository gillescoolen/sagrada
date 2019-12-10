package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public final class PlayerRepository extends Repository<Player> {
    public PlayerRepository(DatabaseConnection connection) {
        super(connection);
    }

    public boolean isPatternCardChosen(Game game) throws SQLException {
        PreparedStatement playerPreparedStatement = this.connection.getConnection().prepareStatement("SELECT COUNT(patterncard_idpatterncard) AS amountOfChosenCards FROM player WHERE spel_idspel = ? AND playstatus_playstatus IN (?, ?)");

        playerPreparedStatement.setInt(1, game.getId());
        playerPreparedStatement.setString(2, PlayStatus.ACCEPTED.getPlayState());
        playerPreparedStatement.setString(3, PlayStatus.CHALLENGER.getPlayState());

        ResultSet resultSet = playerPreparedStatement.executeQuery();

        if (!resultSet.next()) {
            return false;
        }

        return resultSet.getInt("amountOfChosenCards") == game.getPlayers().size();
    }

    public List<Player> prepareAllGamePlayers(Game game) throws SQLException {
        var players = new ArrayList<Player>();
        var random = new Random();
        var privateObjectiveColors = new ArrayList<>(Arrays.asList(Color.values()));
        var sequenceNumber = 1;

        PreparedStatement playerPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player WHERE spel_idspel = ? AND playstatus_playstatus IN (?, ?)");
        playerPreparedStatement.setInt(1, game.getId());
        playerPreparedStatement.setString(2, PlayStatus.ACCEPTED.getPlayState());
        playerPreparedStatement.setString(3, PlayStatus.CHALLENGER.getPlayState());
        ResultSet playerResultSet = playerPreparedStatement.executeQuery();

        var patternCardRepository = new PatternCardRepository(this.connection);
        var patternCards = patternCardRepository.getAllPatternCards();

        while (playerResultSet.next()) {
            var playerId = playerResultSet.getInt("idplayer");
            var randomColor = privateObjectiveColors.get(random.nextInt(privateObjectiveColors.size()));
            privateObjectiveColors.remove(randomColor);

            PreparedStatement playerUpdatePreparedStatement = this.connection.getConnection().prepareStatement("UPDATE player SET private_objectivecard_color = ?, seqnr = ?, isCurrentPlayer = ?, score = ?, invalidframefield = ? WHERE idplayer = ?");

            playerUpdatePreparedStatement.setString(1, randomColor.getDutchColorName());
            playerUpdatePreparedStatement.setInt(2, sequenceNumber);
            playerUpdatePreparedStatement.setInt(3, sequenceNumber == 1 ? 1 : 0);
            playerUpdatePreparedStatement.setInt(4, 0);
            playerUpdatePreparedStatement.setInt(5, 0);
            playerUpdatePreparedStatement.setInt(6, playerId);

            playerUpdatePreparedStatement.executeUpdate();

            PreparedStatement playerFrameRepository = this.connection.getConnection().prepareStatement(
                    "INSERT INTO playerframefield (player_idplayer, position_x, position_y, idgame) VALUES(?, ?, ?, ?);"
            );

            for (int xSquares = 1; xSquares <= 5; ++xSquares) {
                for (int ySquares = 1; ySquares <= 4; ++ySquares) {
                    playerFrameRepository.setInt(1, playerId);
                    playerFrameRepository.setInt(2, xSquares);
                    playerFrameRepository.setInt(3, ySquares);
                    playerFrameRepository.setInt(4, game.getId());

                    playerFrameRepository.addBatch();
                }
            }

            playerFrameRepository.executeBatch();

            for (int insertAmount = 0; insertAmount < 4; ++insertAmount) {
                var randomPatternCard = patternCards.get(random.nextInt(patternCards.size()));
                patternCardRepository.setOption(playerId, randomPatternCard.getId());
                patternCards.remove(randomPatternCard);
            }

            playerUpdatePreparedStatement.close();
            players.add(this.createPlayer(playerResultSet));
            ++sequenceNumber;
        }

        playerPreparedStatement.close();
        playerResultSet.close();

        return players;
    }

    public List<Player> getAllGamePlayers(Game game) throws SQLException {
        var players = new ArrayList<Player>();

        PreparedStatement playerPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player WHERE spel_idspel = ? AND playstatus_playstatus IN (?, ?)");
        playerPreparedStatement.setInt(1, game.getId());
        playerPreparedStatement.setString(2, PlayStatus.ACCEPTED.getPlayState());
        playerPreparedStatement.setString(3, PlayStatus.CHALLENGER.getPlayState());
        ResultSet playerResultSet = playerPreparedStatement.executeQuery();

        while (playerResultSet.next()) {
            players.add(this.createPlayer(playerResultSet));
        }

        playerPreparedStatement.close();
        playerResultSet.close();

        return players;
    }

    @Override
    public Player findById(int id) throws SQLException {
        Player player = null;

        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player WHERE idplayer = ?");
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            player = createPlayer(resultSet);
        }

        preparedStatement.close();
        resultSet.close();

        return player;
    }

    public Player getGamePlayer(String name, Game game) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player WHERE spel_idspel = ? AND username = ?");
        preparedStatement.setInt(1, game.getId());
        preparedStatement.setString(2, name);

        ResultSet resultSet = preparedStatement.executeQuery();

        Player player = null;

        while (resultSet.next()) {
            player = createPlayer(resultSet);
        }

        preparedStatement.close();
        resultSet.close();

        return player;
    }

    public List<Player> getInvitedPlayers(Game game) throws SQLException {
        List<Player> players = new ArrayList<>();
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player WHERE spel_idspel = ? AND playstatus_playstatus = ?");
        preparedStatement.setInt(1, game.getId());
        preparedStatement.setString(2, PlayStatus.INVITED.getPlayState());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Player player = createPlayer(resultSet);
            players.add(player);
        }

        preparedStatement.close();
        resultSet.close();

        return players;
    }

    public List<Player> getAcceptedPlayers(Game game) throws SQLException {
        List<Player> players = new ArrayList<>();
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player where spel_idspel = ? AND playstatus_playstatus = ?");
        preparedStatement.setInt(1, game.getId());
        preparedStatement.setString(2, PlayStatus.ACCEPTED.getPlayState());

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Player player = createPlayer(resultSet);
            players.add(player);
        }

        preparedStatement.close();
        resultSet.close();

        return players;
    }

    public void update(int id) throws SQLException {

    }

    @Override
    public void update(Player player) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("UPDATE player SET `playstatus_playstatus` = ?, `seqnr` = ?, `isCurrentPlayer` = ?, `private_objectivecard_color` = ?, patterncard_idpatterncard = ?, `score` = ?, `invalidframefield` = ? WHERE `idplayer` = ?");

        preparedStatement.setString(1, player.getPlayStatus().getPlayState());
        if (player.getSequenceNumber() == null) {
            preparedStatement.setNull(2, Types.INTEGER);
        } else {
            preparedStatement.setInt(2, player.getSequenceNumber());
        }

        preparedStatement.setByte(3, ((byte) (player.isCurrentPlayer() ? 1 : 0)));

        preparedStatement.setString(4, player.getPrivateObjectiveCard().getColor().getDutchColorName());

        if (player.getPatternCard() == null) {
            preparedStatement.setNull(5, Types.INTEGER);
        } else {
            preparedStatement.setInt(5, player.getPatternCard().getId());
        }

        preparedStatement.setInt(6, player.getScore());
        preparedStatement.setByte(7, ((byte) (player.hasInvalidFrameField() ? 1 : 0)));
        preparedStatement.setInt(8, player.getId());

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    @Override
    public void updateMultiple(Collection<Player> models) throws SQLException {

    }

    @Override
    public void delete(Player model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<Player> models) throws SQLException {

    }

    public void add(Player player, Game game) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("INSERT INTO player (username, spel_idspel, playstatus_playstatus, isCurrentPlayer, private_objectivecard_color, invalidframefield) " +
                        "VALUES(?,?,?,?,?,?)");

        preparedStatement.setString(1, player.getAccount().getUsername());
        preparedStatement.setInt(2, game.getId());
        preparedStatement.setString(3, player.getPlayStatus().getPlayState());
        preparedStatement.setByte(4, ((byte) (player.isCurrentPlayer() ? 1 : 0)));
        preparedStatement.setString(5, player.getPrivateObjectiveCard().getColor().getDutchColorName());
        preparedStatement.setByte(6, ((byte) (player.hasInvalidFrameField() ? 1 : 0)));

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    @Override
    public void add(Player model) throws SQLException {

    }

    @Override
    public void addMultiple(Collection<Player> models) throws SQLException {

    }

    public void declineInvite(String name, Game game) throws SQLException {
        Player playerToDecline = this.getGamePlayer(name, game);

        playerToDecline.setPlayStatus(PlayStatus.DECLINED);

        this.update(playerToDecline);
    }

    public void acceptInvite(String name, Game game) throws SQLException {
        Player playerToAccept = this.getGamePlayer(name, game);

        playerToAccept.setPlayStatus(PlayStatus.ACCEPTED);

        this.update(playerToAccept);
    }

    public Player createPlayer(ResultSet resultSet) throws SQLException {
        Player player = new Player();

        AccountRepository accountRepository = new AccountRepository(this.connection);
        PatternCardRepository patternCardRepository = new PatternCardRepository(this.connection);
        PlayerFrameRepository playerFrameRepository = new PlayerFrameRepository(this.connection);

        player.setId(resultSet.getInt("idplayer"));
        player.setAccount(accountRepository.findByUsername(resultSet.getString("username")));
        int seqnr = resultSet.getInt("seqnr");
        player.setSequenceNumber(seqnr == 0 ? null : seqnr);

        for (Color color : Color.values()) {
            if (color.getDutchColorName().equals(resultSet.getString("private_objectivecard_color"))) {
                player.setPrivateObjectiveCard(new PrivateObjectiveCard(color));
            }
        }

        player.setCurrentPlayer(resultSet.getBoolean("isCurrentPlayer"));

        int patternCardId = resultSet.getInt("patterncard_idpatterncard");

        if (patternCardId != 0) {
            player.setPatternCard(patternCardRepository.findById(patternCardId));
        }

        player.setPlayerFrame(new PatternCard(playerFrameRepository.getSquares(player)));

        for (PlayStatus playStatus : PlayStatus.values()) {
            if (playStatus.getPlayState().equals(resultSet.getString("playstatus_playstatus"))) {
                player.setPlayStatus(playStatus);
            }
        }

        player.addCardOption(patternCardRepository.getCardOptionsByPlayerId(player.getId()));
        player.setScore(resultSet.getInt("score"));
        player.setInvalidFrameField(resultSet.getBoolean("invalidframefield"));

        return player;
    }

    public void bindPatternCardToPlayer(Player player) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("UPDATE player SET patterncard_idpatterncard = ? WHERE idplayer = ?;");

        preparedStatement.setInt(1, player.getPatternCard().getId());
        preparedStatement.setInt(2, player.getId());

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    public void nextPlayerTurn(Player player, Game game) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection()
                .prepareStatement("UPDATE player SET isCurrentPlayer = ? WHERE idplayer = ?;");

        preparedStatement.setBoolean(1, false);
        preparedStatement.setInt(2, player.getId());

        preparedStatement.executeUpdate();
        preparedStatement.close();

        Player nextPlayer = this.getNextGamePlayer(game);

        nextPlayer.setCurrentPlayer(true);
        this.update(nextPlayer);
    }

    private Player getPlayerByGameAndUsername(Game game, String username) throws SQLException {
        PreparedStatement playerIdStatement = this.connection.getConnection().prepareStatement("SELECT idplayer FROM player WHERE spel_idspel = ? AND username = ?;");

        playerIdStatement.setInt(1, game.getId());
        playerIdStatement.setString(2, username);

        ResultSet playerIdResultSet = playerIdStatement.executeQuery();
        playerIdResultSet.next();

        int playerId = playerIdResultSet.getInt("idplayer");

        playerIdStatement.close();
        playerIdResultSet.close();

        return this.findById(playerId);
    }

    public Player getNextGamePlayer(Game game) throws SQLException {
        PreparedStatement maxStatement = this.connection.getConnection().prepareStatement("SELECT username FROM player WHERE spel_idspel = ? AND seqnr = (SELECT MIN(seqnr) FROM player WHERE spel_idspel = ?);");
        maxStatement.setInt(1, game.getId());
        maxStatement.setInt(2, game.getId());

        ResultSet maxResultSet = maxStatement.executeQuery();
        maxResultSet.next();

        String username = maxResultSet.getString("username");

        maxResultSet.close();
        maxStatement.close();
        
        Player nextPlayer = this.getPlayerByGameAndUsername(game, username);

        GameRepository gameRepository = new GameRepository(this.connection);

        gameRepository.updateGamePlayer(nextPlayer, game);

        return nextPlayer;
    }
}
