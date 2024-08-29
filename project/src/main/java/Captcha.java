import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

public class Captcha {
    private final BufferedImage bufferedImage;
    private final String content;
    private final List<Integer> borders;
    private final List<Integer> yUp;
    private final List<Integer> yDown;

    public Captcha(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        this.content = null;
        this.borders = new ArrayList<>();
        this.yUp = new ArrayList<>();
        this.yDown = new ArrayList<>();
    }

    public Captcha(BufferedImage bufferedImage, String content) {
        this.bufferedImage = bufferedImage;
        this.content = content;
        this.borders = new ArrayList<>();
        this.yUp = new ArrayList<>();
        this.yDown = new ArrayList<>();
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void draw() {
        Paint.doIt(this);
    }

    public void clearColor() {
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                if (getColor(i, j).saturated()) {
                    setColor(i, j, RGB.Black);
                } else {
                    setColor(i, j, RGB.White);
                }
            }
        }
    }

    public void countBorders() {
        List<Integer> freqX = frequencyAnalysisX();
        int start = 0;
        for (int i = 0; i < freqX.size(); i++) {
            if (freqX.get(i) > 25) {
                start = i;
                break;
            }
        }
        int end = 0;
        for (int i = freqX.size() - 1; i >= 0; i--) {
            if (freqX.get(i) > 25) {
                end = i;
                break;
            }
        }
        borders.add(start);
        while (borders.get(borders.size() - 1) + 100 < end) {
            int begin = borders.get(borders.size() - 1);
            boolean theEnd = true;
            for (int result = 0; theEnd; result++) {
                for (int i = 0; i < 35 && theEnd; i++) {
                    if (freqX.get(begin + 60 + i) == result) {
                        borders.add(begin + 60 + i);
                        theEnd = false;
                    } else if (freqX.get(begin + 60 - i) == result) {
                        borders.add(begin + 60 - i);
                        theEnd = false;
                    }
                }
            }
        }
        borders.add(end);
    }

    public void countYUpAndDown() {
        for (int i = 1; i < borders.size(); i++) {
            List<Integer> freqY = frequencyAnalysisY(borders.get(i - 1), borders.get(i));
            int start = 0;
            for (int j = start, count = 0; j < freqY.size() / 3; j++) {
                if (freqY.get(j) == 0) {
                    count++;
                }
                if (count > (j - start) / 2) {
                    start = j;
                    count = 0;
                }
            }
            int end = freqY.size() - 1;
            for (int j = end, count = 0; j > (2 * freqY.size()) / 3; j--) {
                if (freqY.get(j) == 0) {
                    count++;
                }
                if (count > (end - j) / 2) {
                    end = j;
                    count = 0;
                }
            }
            for (int j = start; j <= end; j++) {
                if (freqY.get(j) > 7) {
                    yUp.add(j);
                    break;
                }
            }
            for (int j = end; j >= start; j--) {
                if (freqY.get(j) > 7) {
                    yDown.add(j);
                    break;
                }
            }
        }
    }

    public void addBordersDraw() {
        for (int i = 0; i < borders.size() - 1; i++) {
            for (int j = yUp.get(i); j <= yDown.get(i); j++) {
                setColor(borders.get(i), j, new RGB(255, 0, 0));
            }
            for (int j = yUp.get(i); j <= yDown.get(i); j++) {
                setColor(borders.get(i + 1), j, new RGB(255, 0, 0));
            }
            for (int j = borders.get(i); j <= borders.get(i + 1); j++) {
                setColor(j, yUp.get(i), new RGB(255, 0, 0));
            }
            for (int j = borders.get(i); j <= borders.get(i + 1); j++) {
                setColor(j, yDown.get(i), new RGB(255, 0, 0));
            }
        }
    }

    public void clearLine() {
        int depth = 0;
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                if (getColor(i, j).isBlack()) {
                    depth++;
                } else {
                    if (depth < 6) {
                        for (int k = j - 1; k >= j - depth; k--) {
                            setColor(i, k, RGB.White);
                        }
                    }
                    depth = 0;
                }
            }
            if (depth < 6) {
                for (int k = bufferedImage.getHeight() - 2; k >= bufferedImage.getHeight() - 1 - depth; k--) {
                    setColor(i, k, RGB.White);
                }
            }
            depth = 0;
        }

        for (int j = 0; j < bufferedImage.getHeight(); j++) {
            for (int i = 0; i < bufferedImage.getWidth(); i++) {
                if (getColor(i, j).isBlack()) {
                    depth++;
                } else {
                    if (depth < 6) {
                        for (int k = i - 1; k >= i - depth; k--) {
                            setColor(k, j, RGB.White);
                        }
                    }
                    depth = 0;
                }
            }
            if (depth < 6) {
                for (int k = bufferedImage.getWidth() - 2; k >= bufferedImage.getWidth() - 1 - depth; k--) {
                    setColor(k, j, RGB.White);
                }
            }
            depth = 0;
        }
    }

    public RGB getColor(int x, int y) {
        WritableRaster writableRaster = bufferedImage.getRaster();
        int[] pixel = writableRaster.getPixel(x, y, new int[3]);
        return new RGB(pixel[0], pixel[1], pixel[2]);
    }

    public void setColor(int x, int y, RGB rgb) {
        WritableRaster writableRaster = bufferedImage.getRaster();
        int[] pixel = writableRaster.getPixel(x, y, new int[3]);
        pixel[0] = rgb.getR();
        pixel[1] = rgb.getG();
        pixel[2] = rgb.getB();
        writableRaster.setPixel(x, y, pixel);
        bufferedImage.setData(writableRaster);
    }

    public void skeletonization() {
        for (int i = 1; i < bufferedImage.getWidth() - 1; i++) {
            for (int j = 1; j < bufferedImage.getHeight() - 1; j++) {
                for (int iter = 0; iter < 2; iter++) {
                    int p2 = getColor(i, j - 1).getB() / 255 == 1 ? 0 : 1;
                    int p3 = getColor(i + 1, j - 1).getB() / 255 == 1 ? 0 : 1;
                    int p4 = getColor(i + 1, j).getB() / 255 == 1 ? 0 : 1;
                    int p5 = getColor(i + 1, j + 1).getB() / 255 == 1 ? 0 : 1;
                    int p6 = getColor(i, j + 1).getB() / 255 == 1 ? 0 : 1;
                    int p7 = getColor(i - 1, j + 1).getB() / 255 == 1 ? 0 : 1;
                    int p8 = getColor(i - 1, j).getB() / 255 == 1 ? 0 : 1;
                    int p9 = getColor(i - 1, j - 1).getB() / 255 == 1 ? 0 : 1;

                    int A = ((p2 == 0 && p3 == 1) ? 1 : 0) + ((p3 == 0 && p4 == 1) ? 1 : 0) +
                            ((p4 == 0 && p5 == 1) ? 1 : 0) + ((p5 == 0 && p6 == 1) ? 1 : 0) +
                            ((p6 == 0 && p7 == 1) ? 1 : 0) + ((p7 == 0 && p8 == 1) ? 1 : 0) +
                            ((p8 == 0 && p9 == 1) ? 1 : 0) + ((p9 == 0 && p2 == 1) ? 1 : 0);
                    int B = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
                    int m1 = iter == 0 ? (p2 * p4 * p6) : (p2 * p4 * p8);
                    int m2 = iter == 0 ? (p4 * p6 * p8) : (p2 * p6 * p8);

                    if (A == 1 && (B >= 2 && B <= 6) && m1 == 0 && m2 == 0) {
                        setColor(i, j, RGB.White);
                    }
                }
            }
        }
    }

    private List<Integer> frequencyAnalysisX() {
        List<Integer> list = new ArrayList<>();
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            int count = 0;
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                if (getColor(x, y).isBlack()) {
                    count++;
                }
            }
            list.add(count);
        }
        return list;
    }

    private List<Integer> frequencyAnalysisY(int start, int end) {
        List<Integer> list = new ArrayList<>();
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            int count = 0;
            for (int x = start; x <= end; x++) {
                if (getColor(x, y).isBlack()) {
                    count++;
                }
            }
            list.add(count);
        }
        return list;
    }

    public List<Segment> segmentation() {
        List<Segment> segments = new ArrayList<>();
        for (int i = 0; i < borders.size() - 1; i++) {
            List<Integer> xs = new ArrayList<>();
            for (int j = borders.get(i); j <= borders.get(i + 1); j++) {
                int count = 0;
                for (int k = yUp.get(i); k <= yDown.get(i); k++) {
                    if (getColor(j, k).isBlack()) {
                        count++;
                    }
                }
                xs.add(count);
            }
            List<Integer> ys = new ArrayList<>();
            for (int j = yUp.get(i); j <= yDown.get(i); j++) {
                int count = 0;
                for (int k = borders.get(i); k <= borders.get(i + 1); k++) {
                    if (getColor(k, j).isBlack()) {
                        count++;
                    }
                }
                ys.add(count);
            }
            segments.add(new Segment(xs, ys));
        }
        if (content != null) {
            for (int i = 0; i < segments.size() && i < content.length(); i++) {
                segments.get(i).setSymbol(String.valueOf(content.charAt(i)));
            }
        }
        return segments;
    }

}
