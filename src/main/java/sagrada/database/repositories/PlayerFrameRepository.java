package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.PreparedStatement;
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

            var position = new Position(xPosition, yPosition);

            for (var colorEnum : Color.values()) {
                if (colorEnum.getDutchColorName().equals(color)) {
                    actualColor = colorEnum;
                }
            }

            square.setPosition(position);
            square.setColor(actualColor);
            square.setValue(value);

            squares.add(square);
        }

        preparedStatement.close();
        resultSet.close();

        return squares;
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
