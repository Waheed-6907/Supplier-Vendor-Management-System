package ui;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        Color color1 = new Color(15, 32, 39);
        Color color2 = new Color(32, 58, 67);

        GradientPaint gp = new GradientPaint(
                0, 0, color1,
                width, height, color2
        );

        g2.setPaint(gp);
        g2.fillRect(0, 0, width, height);
    }
}