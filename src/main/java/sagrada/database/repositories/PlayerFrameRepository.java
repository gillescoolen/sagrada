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

    public void getPlayerFrame(Game game, Player player, PatternCard patternCard) throws SQLException {
        patternCard.setSquares(this.getSquares(game, player));
    }

    public PatternCard getPlayerFrame(Game game, Player player) throws SQLException {
        return new PatternCard(this.getSquares(game, player));
    }

    private List<Square> getSquares(Game game, Player player) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM playerframefield WHERE idgame = ? AND player_idplayer = ? ORDER BY position_y, position_x;");

        preparedStatement.setInt(1, game.getId());
        preparedStatement.setInt(2, player.getId());

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
