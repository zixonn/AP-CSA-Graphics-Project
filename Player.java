import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.*;

public class Player {

    // Movement
    private KeyList prevKey;
    private static final double JUMP_X = 400;
    private static double JUMP_Y = -175;
    private static final long MOVEMENT_COOLDOWN = 350;
    private long lastMoveTime;
    private boolean isOnWall;
    private boolean jumping;

    // Size and position
    private static final int WIDTH = 50;
    private static final int LENGTH = 50;
    private int direction = 270;
    private double x, y;
  

    // Sprites
    private ImageIcon jump, idle, slideLeft, slideRight;

    // Sound
    private Clip jumpSound;

    public Player(int x1, int y1) {

        x = 45;
        y = y1;

        idle = new ImageIcon("assets/sprites/Idle.png");
        jump = new ImageIcon("assets/sprites/Jump.png");
        slideLeft = new ImageIcon("assets/sprites/SlideLeft.png");
        slideRight = new ImageIcon("assets/sprites/SlideRight.png");

        lastMoveTime = 0;
        isOnWall = true;
        jumping = false;
        prevKey = new KeyList();

        // Load jump clip
        try {
            File jumpFile = new File("assets/sounds/Jump.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(jumpFile);
            jumpSound = AudioSystem.getClip();
            jumpSound.open(audioStream);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int newDir) {
        direction = newDir;
        if (direction != 0 && direction != 90 && direction != 180 && direction != 270)
            direction = 0;
    }

    public void draw(myJFrame frame, Graphics g) {
        ImageIcon toUse;
        if (getDirection() == 90 || getDirection() == 270) {
            toUse = jump;
        }

        if (isOnWall) {
            toUse = (getDirection() == 270) ? slideLeft : slideRight;
        } else {
            toUse = idle;
        }

        Image image = toUse.getImage();
        int centerX = getX();
        int centerY = getY();

        g.drawImage(image, centerX - WIDTH / 2, centerY - LENGTH / 2, WIDTH, LENGTH, frame);

    }

    public void move(int minX, int minY, int maxX, int maxY, KeyList kl) {
        minY = 50;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime >= MOVEMENT_COOLDOWN) {
            if (kl.right && !prevKey.right && (x + WIDTH / 2 < maxX)) {
                x += JUMP_X;
                y = Math.max(y + JUMP_Y, minY);
                lastMoveTime = currentTime;
                jumping = true;
                prevKey.right = true;
                prevKey.left = false; // Ensure opposite direction is reset
                jumpSound.setFramePosition(0); // Rewind sound to the beginning
                jumpSound.start(); // Play the jump sound
            } else if (kl.left && !prevKey.left && (x - WIDTH / 2 > minX)) {
                x -= JUMP_X;
                y = Math.max(y + JUMP_Y, minY);
                lastMoveTime = currentTime;
                jumping = true;
                prevKey.left = true;
                prevKey.right = false;
                jumpSound.setFramePosition(0); // Rewind sound to the beginning
                jumpSound.start(); // Play the jump sound
            } else {
                jumping = false;
            }
        }
    
        isOnWall = (getX() <= 45 || getX() >= 430);
    
        if (isOnWall) {
            double slideVelocity = kl.shift ? 1 : 2.5;
            y += slideVelocity;
        }
    }

    public boolean isTouchingLava(int lavaTop) {
        return (getY() + LENGTH / 2) >= lavaTop;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void turnLeft() {
        setDirection(270);
    }

    public void turnRight() {
        setDirection(90);
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(getX() - WIDTH / 2, getY() - LENGTH / 2, WIDTH, LENGTH);
    }
}
