package sagrada.model;

public class Position {
    private int x = 1;
    private int y = 1;

    public Position(int x, int y) {
        setX(x);
        setY(y);
    }

    private void setX(int x) {
        if (!(x < 1)) {
            if (!(x > 5)) {
                this.x = x;
            } else {
                this.x = 5;
            }
        }
    }

    private void setY(int y) {
        if (!(y < 1)) {
            if (!(y > 4)) {
                this.y = y;
            } else {
                this.y = 4;
            }
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
