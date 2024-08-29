import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BaseSegments {
    private static final int NUMBER = 1300;
    private static List<Segment> segments;

    public static List<Segment> getBaseSegments(boolean mode) {
        if (segments == null) {
            if (mode) {
                generation();
            }
            create();
        }
        return segments;
    }

    private static void create() {
        Gson gson = new Gson();
        segments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("BaseSegments.txt"))) {
            String json = br.readLine();
            while (json != null) {
                segments.add(gson.fromJson(json, Segment.class));
                json = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generation() {
        Gson gson = new Gson();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("BaseSegments.txt"))) {
            try (Stream<Path> paths = Files.walk(Paths.get("captchasFINAL"))) {
                paths.skip(1).limit(NUMBER).forEach(path -> {
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
                            gson.toJson(segment, bw);
                            bw.newLine();
                        }
                        System.out.println(String.valueOf(path.getFileName()).substring(0, 4));
                        System.out.println(segments);
                    } catch (IOException ignored) {}
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
