import javax.swing.*;
import java.awt.*;

public class WarningSign {
    private int x, y;
    private int duration; // Duration to display the warning sign in milliseconds
    private long startTime; // The time when the warning sign starts to display
    private Image warningImage;

    public WarningSign(int x, int y, int duration) {
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();

        // Load the image and scale it
        ImageIcon warningIcon = new ImageIcon("assets/sprites/Warning.png");
        int scaledWidth = 30; 
        int scaledHeight = 30; 
        this.warningImage = warningIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > duration;
    }

    public void draw(Graphics g) {
        g.drawImage(warningImage, x, y, null);
    }
}
