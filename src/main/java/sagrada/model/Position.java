package sagrada.model;

public class Position {
    private int x = 1;
    private int y = 1;

    public Position(int x, int y) {
        setX(x);
        setY(y);
    }

    private void setX(int x) {
        if (x < 1) throw new RuntimeException("X cannot be smaller than 1");

        this.x = Math.min(x, 5);
    }

    private void setY(int y) {
        if (y < 1) throw new RuntimeException("Y cannot be smaller than 1");;

        this.y = Math.min(y, 4);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return "X: " + this.x + " Y: " + this.y;
    }
}
