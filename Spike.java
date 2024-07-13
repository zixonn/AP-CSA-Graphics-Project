import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class Spike {

    private int LENGTH;
    private int WIDTH;

    private double x, y;
    private int direction;

    private ImageIcon Spike;

    public Spike(int l, int w, int x1, int y1, int dir) {
        LENGTH = l;
        WIDTH = w;
        x = x1;
        y = y1;
        direction = dir;
        Spike = new ImageIcon("assets/sprites/Spike.png");
    }

    public void draw(myJFrame frame, Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        Image spikeImg = Spike.getImage();

        // Create a buffered image from the spike image
        BufferedImage bufferedSpikeImg = new BufferedImage(WIDTH, LENGTH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gImg = bufferedSpikeImg.createGraphics();
        gImg.drawImage(spikeImg, 0, 0, WIDTH, LENGTH, null);
        gImg.dispose();

        // Apply the darkening effect using RescaleOp
        float scaleFactor = 0.9f; 
        RescaleOp rescaleOp = new RescaleOp(scaleFactor, 0, null);
        bufferedSpikeImg = rescaleOp.filter(bufferedSpikeImg, null);

        // Apply the opacity effect using AlphaComposite
        float opacity = 1f; 
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // Calculate the center of the image
        int centerX = getX();
        int centerY = getY();

        // Apply the rotation around thed center of the image
        g2d.rotate(Math.toRadians(direction), centerX, centerY);

        // Draw the image with the rotation and opacity applied
        g2d.drawImage(bufferedSpikeImg, centerX - WIDTH / 2, centerY - LENGTH / 2, WIDTH, LENGTH, frame);

        // Dispose of the Graphics2D context to restore the original state
        g2d.dispose();
    }

    public void updatePosition(double newY) {
        y = newY;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(getX() - WIDTH / 2, getY() - LENGTH / 2, WIDTH, LENGTH);
    }
}
