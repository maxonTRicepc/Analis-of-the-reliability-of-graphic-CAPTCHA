import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class Paint extends JPanel {

    private final BufferedImage image;

    private Paint(BufferedImage image) {
        this.image = image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }

    public static void doIt(Captcha captcha) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new Paint(captcha.getBufferedImage()));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(captcha.getBufferedImage().getWidth() + 16,
                captcha.getBufferedImage().getHeight() + 39);
        frame.setVisible(true);
    }
}
