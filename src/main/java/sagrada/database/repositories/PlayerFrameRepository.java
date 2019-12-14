package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PlayerFrameRepository extends Repository<PatternCard> {
    public PlayerFrameRepository(DatabaseConnection connection) {
        super(connection);
    }

    public void getPlayerFrame(Player player) throws SQLException {
        var playerFrame = player.getPlayerFrame();
        playerFrame.setSquares(this.getSquares(player));
    }

    public List<Square> getSquares(Player player) throws SQLException {
        var dieRepository = new DieRepository(this.connection);

        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM playerframefield WHERE player_idplayer = ? ORDER BY position_y, position_x;");

        preparedStatement.setInt(1, player.getId());

        var resultSet = preparedStatement.executeQuery();
        var squares = new ArrayList<Square>();

        while (resultSet.next()) {
            Color actualColor = null;
            var square = new Square();

            final int xPosition = resultSet.getInt("position_x");
            final int yPosition = resultSet.getInt("position_y");
            final String color = resultSet.getString("diecolor");
            final int value = resultSet.getInt("dienumber");
            final int gameId = resultSet.getInt("idgame");

            var die = dieRepository.findById(gameId, value, color);

            var position = new Position(xPosition, yPosition);

            for (var colorEnum : Color.values()) {
                if (colorEnum.getDutchColorName().equals(color)) {
                    actualColor = colorEnum;
                }
            }

            square.setPosition(position);
            square.setColor(actualColor);
            square.setValue(value);

            square.setDie(die);

            squares.add(square);
        }

        preparedStatement.close();
        resultSet.close();

        return squares;
    }

    public void updateSquare(Player player, Square square, Die die) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("UPDATE playerframefield SET dienumber = ?, diecolor = ? WHERE player_idplayer = ? AND position_x = ? AND position_y = ?");

        preparedStatement.setInt(1, die.getNumber());
        preparedStatement.setString(2, die.getColor().getDutchColorName());
        preparedStatement.setInt(3, player.getId());
        preparedStatement.setInt(4, square.getPosition().getX());
        preparedStatement.setInt(5, square.getPosition().getY());

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    public void removeSquare(Player player, Square square) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("UPDATE playerframefield SET dienumber = null , diecolor = null WHERE player_idplayer = ? AND position_x = ? AND position_y = ?");

        preparedStatement.setInt(1, player.getId());
        preparedStatement.setInt(2, square.getPosition().getX());
        preparedStatement.setInt(3, square.getPosition().getY());

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    public void invalidateCard(Player player) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("UPDATE player SET invalidframefield = true WHERE idplayer = ?;");

        preparedStatement.setInt(1, player.getId());
        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    public void setCardValid(Player player) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("UPDATE player SET invalidframefield = false WHERE idplayer = ?;");

        preparedStatement.setInt(1, player.getId());
        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    public boolean checkIfCardIsValid(Player player) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT invalidframefield FROM player WHERE idplayer = ?;");

        preparedStatement.setInt(1, player.getId());

        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        boolean invalidFrameField = resultSet.getBoolean("invalidframefield");

        preparedStatement.close();
        resultSet.close();

        return invalidFrameField;
    }

    @Override
    public PatternCard findById(int id) throws SQLException {
        return null;
    }

    @Override
    public void update(PatternCard model) throws SQLException {

    }

    @Override
    public void updateMultiple(Collection<PatternCard> models) throws SQLException {

    }

    @Override
    public void delete(PatternCard model) throws SQLException {

    }

    @Override
    public void deleteMultiple(Collection<PatternCard> models) throws SQLException {

    }

    @Override
    public void add(PatternCard model) throws SQLException {

    }

    @Override
    public void addMultiple(Collection<PatternCard> models) throws SQLException {

    }
}
