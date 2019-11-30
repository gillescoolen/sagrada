package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class PlayerRepository extends Repository<Player> {
    public PlayerRepository(DatabaseConnection connection) {
        super(connection);
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
        preparedStatement.setByte(4, ((byte)0));
        preparedStatement.setString(5, player.getPrivateObjectiveCard().getColor().getColor());

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
