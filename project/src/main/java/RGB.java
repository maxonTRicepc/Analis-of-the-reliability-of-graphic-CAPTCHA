public class RGB {

    public static final RGB White = new RGB(255, 255, 255);
    public static final RGB Black = new RGB(0, 0, 0);

    private final int R;
    private final int G;
    private final int B;

    public RGB(int r, int g, int b) {
        R = r;
        G = g;
        B = b;
    }

    public int getR() {
        return R;
    }

    public int getG() {
        return G;
    }

    public int getB() {
        return B;
    }

    public boolean isBlack() {
        return R == 0 && G == 0 && B == 0;
    }

    public boolean saturated() {
        return R < 200 && G < 200;
    }

    @Override
    public String toString() {
        return "RGB{" +
                "R=" + R +
                ", G=" + G +
                ", B=" + B +
                '}';
    }
}
