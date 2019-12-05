package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public final class PlayerRepository extends Repository<Player> {
    public PlayerRepository(DatabaseConnection connection) {
        super(connection);
    }

    public List<Player> getAllGamePlayers(int gameId) throws SQLException {
        var players = new ArrayList<Player>();
        var privateObjectiveColors = Arrays.asList(Color.values());
        var random = new Random();
        var sequenceNumber = 1;

        PreparedStatement playerPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player WHERE spel_idspel = ?");
        playerPreparedStatement.setInt(1, gameId);
        ResultSet playerResultSet = playerPreparedStatement.executeQuery();

        if (playerResultSet.getFetchSize() < 2) {
            return null;
        }

        var patternCardRepository = new PatternCardRepository(this.connection);
        var patternCards = patternCardRepository.getAllPatternCards();

        while (playerResultSet.next()) {
            var player = new Player();
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

            player.setPrivateObjectiveCard(new PrivateObjectiveCard(randomColor));
            player.setCurrentPlayer(sequenceNumber == 1);
            player.setId(playerId);
            player.setInvalidFrameField(false);
            player.setScore(0);
            player.setPlayStatus(PlayStatus.ACCEPTED);
            player.setSequenceNumber(sequenceNumber);

            for (int insertAmount = 0; insertAmount < 4; ++insertAmount) {
                var randomPatternCard = patternCards.get(random.nextInt(patternCards.size()));
                patternCardRepository.setOption(playerId, randomPatternCard.getId());
                player.addCardOption(randomPatternCard);
                patternCards.remove(randomPatternCard);
            }

            players.add(player);
            ++sequenceNumber;
        }

        return players;
    }

    @Override
    public Player findById(int id) throws SQLException {
        Player player = new Player();
        AccountRepository accountRepository = new AccountRepository(this.connection);
        PatternCardRepository patternCardRepository = new PatternCardRepository(this.connection);

        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM player WHERE idplayer = ?");
        preparedStatement.setInt(1, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            player.setId(resultSet.getInt("idplayer"));
            player.setAccount(accountRepository.findByUsername(resultSet.getString("username")));
            player.setSequenceNumber(resultSet.getInt("seqnr"));

            for (Color color : Color.values()) {
                if (color.getColor().equals(resultSet.getString("private_objectivecard_color"))) {
                    player.setPrivateObjectiveCard(new PrivateObjectiveCard(color));
                }
            }

            player.setCurrentPlayer(resultSet.getBoolean("isCurrentPlayer"));
            player.setPatternCard(patternCardRepository.findById(resultSet.getInt("patterncard_idpatterncard")));

            for (PlayStatus playStatus : PlayStatus.values()) {
                if (player.getPlayStatus().getPlayState().equals(resultSet.getString("playstatus_playstatus"))) {
                    player.setPlayStatus(playStatus);
                }
            }

            player.setScore(resultSet.getInt("score"));
        }

        preparedStatement.close();
        resultSet.close();

        return player;
    }

    public void update(int id) throws SQLException {
        
    }

    @Override
    public void update(Player model) throws SQLException {

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
                .prepareStatement("INSERT INTO player (username, spel_idspel, playstatus_playstatus, isCurrentPlayer, private_objectivecard_color) " +
                        "VALUES(?,?,?,?,?)");

        preparedStatement.setString(1, player.getAccount().getUsername());
        preparedStatement.setInt(2, game.getId());
        preparedStatement.setString(3, player.getPlayStatus().getPlayState());
        preparedStatement.setByte(4, ((byte) (player.isCurrentPlayer() ? 1 : 0)));
        preparedStatement.setString(5, player.getPrivateObjectiveCard().getColor().getDutchColorName());

        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    @Override
    public void add(Player model) throws SQLException {

    }

    @Override
    public void addMultiple(Collection<Player> models) throws SQLException {

    }
}
