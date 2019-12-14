package sagrada.database.repositories;

import sagrada.database.DatabaseConnection;
import sagrada.model.Color;
import sagrada.model.PatternCard;
import sagrada.model.Position;
import sagrada.model.Square;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PatternCardRepository extends Repository<PatternCard> {

    public PatternCardRepository(DatabaseConnection connection) {
        super(connection);
    }

    public void setOption(int playerId, int patternCardId) throws SQLException {
        PreparedStatement patternCardOptionPreparedStatement = this.connection.getConnection().prepareStatement("INSERT INTO patterncardoption (patterncard_idpatterncard, player_idplayer) VALUES (?, ?)");

        patternCardOptionPreparedStatement.setInt(1, patternCardId);
        patternCardOptionPreparedStatement.setInt(2, playerId);

        patternCardOptionPreparedStatement.executeUpdate();
        patternCardOptionPreparedStatement.close();
    }

    public List<PatternCard> getCardOptionsByPlayerId(int playerId) throws SQLException {
        PreparedStatement patternCardPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM patterncardoption WHERE player_idplayer = ?");
        patternCardPreparedStatement.setInt(1, playerId);
        ResultSet patternCardResultSet = patternCardPreparedStatement.executeQuery();

        List<PatternCard> patternCards = new ArrayList<>();

        while (patternCardResultSet.next()) {
            var patternCardId = patternCardResultSet.getInt("patterncard_idpatterncard");
            var patternCard = this.findById(patternCardId);
            patternCards.add(patternCard);
        }

        patternCardPreparedStatement.close();
        patternCardResultSet.close();

        return patternCards;
    }

    public List<PatternCard> getAllPatternCards() throws SQLException {
        var patternCards = new ArrayList<PatternCard>();

        PreparedStatement patternCardPreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM patterncard");
        ResultSet patternCardResultSet = patternCardPreparedStatement.executeQuery();

        while (patternCardResultSet.next()) {
            var squares = new ArrayList<Square>();
            var id = patternCardResultSet.getInt("idpatterncard");
            var name = patternCardResultSet.getString("name");
            var difficulty = patternCardResultSet.getInt("difficulty");
            var standard = patternCardResultSet.getInt("standard");

            PreparedStatement squarePreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM patterncardfield WHERE patterncard_idpatterncard = ? ORDER BY position_y, position_x;");

            squarePreparedStatement.setInt(1, id);
            ResultSet squareResultSet = squarePreparedStatement.executeQuery();

            while (squareResultSet.next()) {
                Color actualColor = null;
                var square = new Square();

                final int xPosition = squareResultSet.getInt("position_x");
                final int yPosition = squareResultSet.getInt("position_y");
                final String color = squareResultSet.getString("color");
                final int value = squareResultSet.getInt("value");

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

            squarePreparedStatement.close();
            squareResultSet.close();

            patternCards.add(new PatternCard(id, name, difficulty, standard, squares));
        }

        patternCardPreparedStatement.close();
        patternCardResultSet.close();

        return patternCards;
    }

    @Override
    public PatternCard findById(int id) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM patterncard WHERE idpatterncard = ?;");

        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.next()) {
            return null;
        }

        final int idPatternCard = resultSet.getInt("idpatterncard");
        final String name = resultSet.getString("name");
        final int difficulty = resultSet.getInt("difficulty");
        final int standard = resultSet.getInt("standard");

        List<Square> squares = new ArrayList<>();

        PreparedStatement squarePreparedStatement = this.connection.getConnection().prepareStatement("SELECT * FROM patterncardfield WHERE patterncard_idpatterncard = ? ORDER BY position_y, position_x;");

        squarePreparedStatement.setInt(1, idPatternCard);
        ResultSet squareResultSet = squarePreparedStatement.executeQuery();

        while (squareResultSet.next()) {
            Color actualColor = null;
            var square = new Square();

            final int xPosition = squareResultSet.getInt("position_x");
            final int yPosition = squareResultSet.getInt("position_y");
            final String color = squareResultSet.getString("color");
            final int value = squareResultSet.getInt("value");

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

        squarePreparedStatement.close();
        squareResultSet.close();
        resultSet.close();
        preparedStatement.close();

        return new PatternCard(idPatternCard, name, difficulty, standard, squares);
    }

    @Override
    public void update(PatternCard patternCard) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "UPDATE patterncard SET `name` = ?, `difficulty` = ?, `standard` = ? WHERE `idpatterncard` = ?;"
        );

        preparedStatement.setString(1, patternCard.getName());
        preparedStatement.setInt(2, patternCard.getDifficulty());
        preparedStatement.setInt(3, patternCard.getStandard());

        preparedStatement.setInt(4, patternCard.getId());

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    @Override
    public void updateMultiple(Collection<PatternCard> patternCards) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "UPDATE patterncard SET `name` = ?, `difficulty` = ?, `standard` = ? WHERE `idpatterncard` = ?;"
        );

        int count = 0;

        for (PatternCard patternCard : patternCards) {
            preparedStatement.setString(1, patternCard.getName());
            preparedStatement.setInt(2, patternCard.getDifficulty());
            preparedStatement.setInt(3, patternCard.getStandard());
            preparedStatement.setInt(4, patternCard.getId());

            preparedStatement.addBatch();

            count++;

            if (count % BATCH_SIZE == 0 || count == patternCards.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    @Override
    public void delete(PatternCard patternCard) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("DELETE FROM patterncard WHERE idpatterncard = ?;");

        preparedStatement.setInt(1, patternCard.getId());

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    @Override
    public void deleteMultiple(Collection<PatternCard> patternCards) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "DELETE FROM patterncard WHERE idpatterncard = ?;"
        );

        int count = 0;

        for (PatternCard patternCard : patternCards) {
            preparedStatement.setInt(1, patternCard.getId());

            preparedStatement.addBatch();

            count++;

            if (count % BATCH_SIZE == 0 || count == patternCards.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }

    @Override
    public void add(PatternCard patternCard) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement("INSERT INTO patterncard values(?, ?, ?);");

        preparedStatement.setString(1, patternCard.getName());
        preparedStatement.setInt(2, patternCard.getDifficulty());
        preparedStatement.setInt(3, patternCard.getStandard());

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    @Override
    public void addMultiple(Collection<PatternCard> patternCards) throws SQLException {
        PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(
                "INSERT INTO patterncard values(?, ?, ?);"
        );

        int count = 0;

        for (PatternCard patternCard : patternCards) {
            preparedStatement.setString(1, patternCard.getName());
            preparedStatement.setInt(2, patternCard.getDifficulty());
            preparedStatement.setInt(3, patternCard.getStandard());

            preparedStatement.addBatch();

            count++;

            if (count % BATCH_SIZE == 0 || count == patternCards.size()) {
                preparedStatement.executeBatch();
            }
        }

        preparedStatement.close();
    }
}
