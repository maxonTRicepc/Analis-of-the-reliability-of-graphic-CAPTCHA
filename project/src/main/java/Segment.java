import java.util.List;

public class Segment {

    private final List<Integer> xs;
    private final List<Integer> ys;
    private String symbol;

    public Segment(List<Integer> xs, List<Integer> ys) {
        this.xs = xs;
        this.ys = ys;
        this.symbol = "";
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double similarity(Segment segment) {
        double numberOfCoincidencesX = 0;
        double numberX = 0;
        int i = 0;
        for (; i < this.xs.size() && i < segment.xs.size(); i++) {
            if (this.xs.get(i).equals(segment.xs.get(i))) {
                numberOfCoincidencesX++;
            }
            numberX++;
        }
        numberX += this.xs.size() - i + segment.xs.size() - i;

        double numberOfCoincidencesY = 0;
        double numberY = 0;
        int j = 0;
        for (; j < this.ys.size() && j < segment.ys.size(); j++) {
            if (this.ys.get(j).equals(segment.ys.get(j))) {
                numberOfCoincidencesY++;
            }
            numberY++;
        }
        numberY += this.ys.size() - j + segment.ys.size() - j;

        return (numberOfCoincidencesX / numberX) * (numberOfCoincidencesY / numberY);
    }

    @Override
    public String toString() {
        return "Segment{" +
                "xs=" + xs +
                ", ys=" + ys +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
