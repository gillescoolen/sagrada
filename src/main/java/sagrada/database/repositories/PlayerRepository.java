package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerRepository extends Repository<Player> {
    public PlayerRepository(DatabaseConnection connection) {
        super(connection);
    }

    @Override
    public Player findById(int id) throws SQLException {
        Player player = null;
        AccountRepository accountRepository = new AccountRepository(this.connection);
        PatternCardRepository patternCardRepository = new PatternCardRepository(this.connection);

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
        preparedStatement.setByte(6,((byte) (player.hasInvalidFrameField() ? 1 : 0)));

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

        for (PlayStatus playStatus : PlayStatus.values()) {
            if (playStatus.getPlayState().equals(resultSet.getString("playstatus_playstatus"))) {
                player.setPlayStatus(playStatus);
            }
        }

        player.setScore(resultSet.getInt("score"));
        player.setInvalidFrameField(resultSet.getBoolean("invalidframefield"));

        return player;
    }
}
