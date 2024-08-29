import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        List<Segment> baseSegments = BaseSegments.getBaseSegments(false);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Result.txt"))) {
            try (Stream<Path> paths = Files.walk(Paths.get("captchasFINAL"))) {
                int[] four = new int[5];
                int[] five = new int[6];
                paths.skip(1301).forEach(path -> {
                    String name = String.valueOf(path.getFileName());
                    name = name.substring(5);
                    String content = name.substring(0, name.length() - 4);
                    try {
                        Captcha captcha = new Captcha(ImageIO.read(new File(String.valueOf(path))), content);
                        captcha.clearColor();
                        captcha.countBorders();
                        captcha.clearLine();
                        captcha.countYUpAndDown();
                        captcha.skeletonization();
                        List<Segment> segments = captcha.segmentation();
                        StringBuilder result = new StringBuilder();
                        for (Segment segment : segments) {
                            if (segment.getSymbol() == null || segment.getSymbol().length() != 1) {
                                continue;
                            }
                            double probability = -1;
                            Segment suitable = segment;
                            for (Segment baseSegment : baseSegments) {
                                if (baseSegment.getSymbol() == null || baseSegment.getSymbol().length() != 1) {
                                    continue;
                                }
                                double prob = segment.similarity(baseSegment);
                                if (probability < prob) {
                                    probability = prob;
                                    suitable = baseSegment;
                                }
                            }
                            result.append(suitable.getSymbol());
                        }
                        System.out.println(String.valueOf(path.getFileName()).substring(0, 4) + "!!!!!!!!!!!!!!!!!!!!!!!!");
                        System.out.println(content + " - " + result);
                        int count = 0;
                        for (int i = 0; i < content.length() && i < result.length(); i++) {
                            if (content.charAt(i) == result.charAt(i)) {
                                count++;
                            }
                        }
                        if (content.length() == 4) {
                            four[count]++;
                        } else {
                            five[count]++;
                        }
                    } catch (IOException ignored) {}
                    System.out.println(Arrays.toString(four));
                    System.out.println(Arrays.toString(five));
                });
                System.out.println(Arrays.toString(four));
                System.out.println(Arrays.toString(five));
                bw.write(Arrays.toString(four));
                bw.newLine();
                bw.write(Arrays.toString(five));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void base(String[] args) {
        List<Segment> baseSegments = BaseSegments.getBaseSegments(false);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Result.txt"))) {
            try (Stream<Path> paths = Files.walk(Paths.get("captchasFINAL"))) {
                Map<String, Integer> numberMeetings = new HashMap<>();
                Map<String, Integer> numberRecognitions = new HashMap<>();
                Map<String, Set<String>> confused = new HashMap<>();
                paths.skip(1301).forEach(path -> {
                    String name = String.valueOf(path.getFileName());
                    name = name.substring(5);
                    String content = name.substring(0, name.length() - 4);
                    try {
                        Captcha captcha = new Captcha(ImageIO.read(new File(String.valueOf(path))), content);
                        captcha.clearColor();
                        captcha.countBorders();
                        captcha.clearLine();
                        captcha.countYUpAndDown();
                        captcha.skeletonization();
                        List<Segment> segments = captcha.segmentation();
                        for (Segment segment : segments) {
                            if (segment.getSymbol() == null || segment.getSymbol().length() != 1) {
                                continue;
                            }
                            double probability = -1;
                            Segment suitable = segment;
                            for (Segment baseSegment : baseSegments) {
                                if (baseSegment.getSymbol() == null || baseSegment.getSymbol().length() != 1) {
                                    continue;
                                }
                                double prob = segment.similarity(baseSegment);
                                if (probability < prob) {
                                    probability = prob;
                                    suitable = baseSegment;
                                }
                            }
                            if (numberMeetings.containsKey(segment.getSymbol())) {
                                numberMeetings.put(segment.getSymbol(), numberMeetings.get(segment.getSymbol()) + 1);
                            } else {
                                numberMeetings.put(segment.getSymbol(), 1);
                            }
                            if (segment.getSymbol().equals(suitable.getSymbol())) {
                                if (numberRecognitions.containsKey(segment.getSymbol())) {
                                    numberRecognitions.put(segment.getSymbol(), numberRecognitions.get(segment.getSymbol()) + 1);
                                } else {
                                    numberRecognitions.put(segment.getSymbol(), 1);
                                }
                            } else {
                                if (confused.containsKey(segment.getSymbol())) {
                                    confused.get(segment.getSymbol()).add(suitable.getSymbol());
                                } else {
                                    Set<String> set = new HashSet<>();
                                    set.add(suitable.getSymbol());
                                    confused.put(segment.getSymbol(), set);
                                }
                            }
                        }
                    } catch (IOException ignored) {}
                    System.out.println(String.valueOf(path.getFileName()).substring(0, 4) + "!!!!!!!!!!!!!!!!!!!!!!!!");
                    for (String symbol: numberMeetings.keySet()) {
                        System.out.print(symbol + " ");
                        System.out.print(numberMeetings.get(symbol) + " ");
                        if (numberRecognitions.containsKey(symbol)) {
                            System.out.print(numberRecognitions.get(symbol) + " ");
                            System.out.print(
                                    ((int)((((double)numberRecognitions.get(symbol)) / numberMeetings.get(symbol)) * 100))
                                            + "% ");
                        } else {
                            System.out.print("0 ");
                            System.out.print("0% ");
                        }
                        if (confused.containsKey(symbol)) {
                            for (String confusedSymbol : confused.get(symbol)) {
                                System.out.print(confusedSymbol + " ");
                            }
                        }
                        System.out.println();
                    }
                });
                for (String symbol: numberMeetings.keySet()) {
                    System.out.print(symbol + " ");
                    bw.write(symbol + " ");

                    System.out.print(numberMeetings.get(symbol) + " ");
                    bw.write(numberMeetings.get(symbol) + " ");

                    if (numberRecognitions.containsKey(symbol)) {
                        System.out.print(numberRecognitions.get(symbol) + " ");
                        bw.write(numberRecognitions.get(symbol) + " ");
                        System.out.print(
                                ((int)((((double)numberRecognitions.get(symbol)) / numberMeetings.get(symbol)) * 100))
                                        + "% ");
                        bw.write(
                                ((int)((((double)numberRecognitions.get(symbol)) / numberMeetings.get(symbol)) * 100))
                                        + "% ");
                    } else {
                        System.out.print("0 ");
                        bw.write("0 ");
                        System.out.print("0% ");
                        bw.write("0% ");
                    }

                    if (confused.containsKey(symbol)) {
                        for (String confusedSymbol : confused.get(symbol)) {
                            System.out.print(confusedSymbol + " ");
                            bw.write(confusedSymbol + " ");
                        }
                    }

                    System.out.println();
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void singleCaptcha() {
        try (Stream<Path> paths = Files.walk(Paths.get("captchasFINAL"))) {
            Captcha captcha = new Captcha(ImageIO.read(new File(String.valueOf(paths.skip(1983).findFirst().get()))));
            captcha.draw();
            captcha.clearColor();
            captcha.draw();
            captcha.countBorders();
            captcha.clearLine();
            captcha.draw();
            captcha.countYUpAndDown();
            captcha.skeletonization();
            captcha.draw();
            captcha.addBordersDraw();
            captcha.draw();
            List<Segment> segments = captcha.segmentation();
            System.out.println(segments);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<Character, Integer> frequencyAnalysisContent() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get("captchasFINAL"))) {
            Map<Character, Integer> map = new HashMap<>();
            paths.skip(1).forEach(path -> {
                String name = String.valueOf(path.getFileName());
                name = name.substring(5);
                name = name.substring(0, name.length() - 4);
                for (int i = 0; i < name.length(); i++) {
                    if (map.containsKey(name.charAt(i))) {
                        map.put(name.charAt(i), map.get(name.charAt(i)) + 1);
                    } else {
                        map.put(name.charAt(i), 1);
                    }
                }
            });
            return map;
        }
    }

}
