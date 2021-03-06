package sagrada.model;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.DieRepository;
import sagrada.database.repositories.PlayerFrameRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class PatternCard extends ObservableCard<PatternCard> {
    private int id;
    private int difficulty;
    private int standard;

    private List<Square> squares;

    public PatternCard(int id, String name, int difficulty, int standard, List<Square> squares) {
        super(name);
        this.id = id;
        this.difficulty = difficulty;
        this.standard = standard;
        this.squares = squares;
    }

    public PatternCard(List<Square> squares) {
        super("player field");
        this.squares = squares;
    }

    public int getId() {
        return this.id;
    }

    public int getDifficulty() {
        return this.difficulty;
    }

    public int getStandard() {
        return this.standard;
    }

    public Square getSquareByXAndY(int x, int y) {
        for (var square : this.squares) {
            var position = square.getPosition();

            if (position.getX() == x && position.getY() == y) {
                return square;
            }
        }

        return null;
    }

    public List<Square> getSquares() {
        return List.copyOf(this.squares);
    }

    public void setSquares(List<Square> squares) {
        this.squares = squares;
        this.update(this);
    }

    public void replaceSquare(Square oldSquare, Square newSquare) {
        this.squares.set(this.squares.indexOf(oldSquare), newSquare);
        this.update(this);
    }

    public void moveDie(Player player, Square oldSquare, Square newSquare, DatabaseConnection connection) {
        var dieToMove = oldSquare.getDie();

        this.placeDie(player, newSquare, dieToMove, connection);
        this.removeDie(player, oldSquare, connection);
    }

    public void placeDie(Player player, Square square, Die die, DatabaseConnection connection) {
        var foundSquare = this.getSquareByXAndY(square.getPosition().getX(), square.getPosition().getY());
        if (foundSquare == null) return;

        this.update(this);

        try {
            PlayerFrameRepository playerFrameRepository = new PlayerFrameRepository(connection);
            playerFrameRepository.updateSquare(player, foundSquare, die);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeDie(Player player, Square square, DatabaseConnection connection) {
        var foundSquare = this.getSquareByXAndY(square.getPosition().getX(), square.getPosition().getY());
        if (foundSquare == null) return;

        try {
            PlayerFrameRepository playerFrameRepository = new PlayerFrameRepository(connection);
            playerFrameRepository.removeSquare(player, foundSquare);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        foundSquare.setDie(null);
        this.update(this);
    }

    public int countEmptySquares() {
        return ((int) this.squares.stream().filter(square -> square.getDie() == null).count());
    }
}
