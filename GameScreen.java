import javax.swing.*;
import javax.swing.text.html.HTMLDocument.Iterator;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends JPanel {

    // Objects
    KeyList KL;
    Player player;
    Timer gameTimer;
    myJFrame frame;

    // Lava
    private int panelHeight = 700;
    private int lavaTop = 600;
    private float riseFactor = 1.0f;
    private double riseFactorIncrement = 1 * (Math.pow(10,-10));

    // Spikes and generation
    private Random random = new Random();
    private ArrayList<Spike> spikes;
    private ArrayList<WarningSign> warningSigns; 
    private long lastSpikeGenerationTime = 0;
    private int spikeFrequency = 5000;
    private int spikeWidth = 25;
    private int spikeHeight = 25;
    private int warningDuration = 2000; 

    // Background and lava images
    private Image backgroundImage;
    private Image lavaSurfaceImage;
    private Image lavaBodyImage;
    private Image leftWallImage;
    private Image rightWallImage;

    // Game Manager
    private WAVPlayer wavPlayer;
    private boolean gameOver = false;
    private long gameStartTime; 


       
    public GameScreen(KeyList KL, myJFrame frame) {
        this.KL = KL;
        this.frame = frame;

        // Initialize coordinates of the player
        player = new Player(45, panelHeight - 500);

        // Create a timer to update the game screen periodically
        int delay = 1000 / 360; // 360 frames per second
        gameTimer = new Timer(delay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repaint();
                updateGame();
            }
        });

        // Make spike stuff
        random = new Random();
        spikes = new ArrayList<>();
        warningSigns = new ArrayList<>(); 

        // Pre-generate spikes
        preGenerateSpikes(-1); 

        // Load the background and lava images
        backgroundImage = new ImageIcon("assets/sprites/background.png").getImage();
        lavaSurfaceImage = new ImageIcon("assets/sprites/Lava.png").getImage();
        lavaBodyImage = new ImageIcon("assets/sprites/LavaBody.png").getImage();
        leftWallImage = new ImageIcon("assets/sprites/LeftWall.png").getImage();
        rightWallImage = new ImageIcon("assets/sprites/RightWall.png").getImage();

        //Load soundtrack
        wavPlayer = new WAVPlayer("assets/sounds/Wandering.wav");
        wavPlayer.play();

        
        //Start the game and scoring
        startGame();
        gameStartTime = System.currentTimeMillis();
    }

    public void startGame() {
        gameTimer.start();
    }

    private void updateGame() {
        if (gameOver) {
            return;
        }
    
        riseFactor += riseFactorIncrement;
        riseFactorIncrement += 1 * (Math.pow(10,-7));
        lavaTop -= riseFactor;
    
        Dimension window = frame.getSize();
    
        // Set maximum bounds to allow the player to move to the edges
        int maxX = (int) window.getWidth();
        int maxY = (int) window.getHeight();
    
        player.move(0, 0, maxX, maxY, KL);
    
        if (KL.left) {
            player.turnLeft();
        }
        if (KL.right) {
            player.turnRight();
        }
    
        if (player.isJumping()) {
            for (Spike spike : spikes) {
                spike.updatePosition(spike.getY() + riseFactor);
            }
        }
    
        if (player.isTouchingLava(lavaTop)) {
            System.out.println("Player touched the lava!");
            gameTimer.stop();
            gameOver = true;
        }
    
        // Increase spike frequency over time
        spikeFrequency -= 5; 
        spikeFrequency = Math.max(spikeFrequency, 1000); 
    
        // Generate spikes periodically
        if (System.currentTimeMillis() - lastSpikeGenerationTime >= spikeFrequency) {
            generateSpikes(1); 
            lastSpikeGenerationTime = System.currentTimeMillis();
        }
    
        // Check collision with spikes
        for (Spike spike : spikes) {
            if (checkCollision(player, spike)) {
                System.out.println("Player touched the spike!");
                gameTimer.stop();
                gameOver = true;
                break; // Exit loop if collision detected
            }
        }
    
        // Update warning signs
        warningSigns.removeIf(warningSign -> warningSign.isExpired());
         
    
        if (gameOver) {
            long score = calculateScore(); 
            gameOver(score); 
            return;
        }
    }

    private void preGenerateSpikes(int initialSpikeCount) {
        generateSpikes(initialSpikeCount);
    }

    private void generateSpikes(int numberOfSpikes) {
        int minDistanceBetweenSpikes = 75; // Define the minimum distance between spikes
        int playerBufferZone = 150; // Define the buffer zone around the player
    
        for (int i = 0; i < numberOfSpikes; i++) {
            double rand = Math.random();
            if (rand <= 0.5) {
                // Generate spikes on the left wall
                int spikeXLeft = 40;
                int spikeYLeft = random.nextInt(player.getX() + 300) + 85;
    
                // Check distance from existing spikes and player
                boolean validPosition = true;
                for (Spike existingSpike : spikes) {
                    if (Math.abs(existingSpike.getY() - spikeYLeft) < minDistanceBetweenSpikes
                            || Math.abs(player.getX() - spikeXLeft) < playerBufferZone) {
                        validPosition = false;
                        break;
                    }
                }
    
                // Add warning sign if position is valid
                if (validPosition) {
                    warningSigns.add(new WarningSign(spikeXLeft, spikeYLeft, warningDuration));
                    Timer timer = new Timer(warningDuration, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            spikes.add(new Spike(spikeWidth, spikeHeight, spikeXLeft, spikeYLeft, 0));
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            } else {
                // Generate spikes on the right wall
                int spikeXRight = 445;
                int spikeYRight = random.nextInt(lavaTop - 100);
    
                // Check distance from existing spikes and player
                boolean validPosition = true;
                for (Spike existingSpike : spikes) {
                    if (Math.abs(existingSpike.getY() - spikeYRight) < minDistanceBetweenSpikes
                            || Math.abs(player.getX() - spikeXRight) < playerBufferZone) {
                        validPosition = false;
                        break;
                    }
                }
    
                // Add warning sign if position is valid
                if (validPosition) {
                    warningSigns.add(new WarningSign(spikeXRight - 25, spikeYRight, warningDuration));
                    Timer timer = new Timer(warningDuration, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            spikes.add(new Spike(spikeWidth, spikeHeight, spikeXRight, spikeYRight, 180));
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }
    }
    

    public boolean checkCollision(Player player, Spike spike) {
        Rectangle playerBox = player.getBoundingBox();
        Rectangle spikeBox = spike.getBoundingBox();
        return playerBox.intersects(spikeBox);
    }

    private long calculateScore() {
        long gameDuration = System.currentTimeMillis() - gameStartTime;
        return gameDuration / 1000; 
    }

    public void stopMusic() {
        wavPlayer.stop();
    }
    
    private void gameOver(long score) {
        // Stop the game timer
        gameTimer.stop();
        stopMusic();
    
        // Create the game over panel with the player's score
        GameOverPanel gameOverPanel = new GameOverPanel(score, this);
JFrame gameOverFrame = new JFrame();
gameOverFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
gameOverFrame.setContentPane(gameOverPanel);
gameOverFrame.pack();
gameOverFrame.setLocationRelativeTo(null);
gameOverFrame.setVisible(true);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Apply a darker overlay on top of the background image
        g.setColor(new Color(0, 0, 0, 25)); 
        g.fillRect(0, 0, getWidth(), getHeight());

        player.draw(frame, g);

        // Draw warning signs
        for (WarningSign warningSign : warningSigns) {
            warningSign.draw(g);
        }

        // Draw spikes
        for (Spike spike : spikes) {
            spike.draw(frame, g);
        }

        // Draw the left wall image
        int leftWallWidth = leftWallImage.getWidth(null);
        int numLeftWallTiles = (int) Math.ceil((double) panelHeight / leftWallImage.getHeight(null)); 
        for (int i = 0; i < numLeftWallTiles; i++) {
            g.drawImage(leftWallImage, 0, i * leftWallImage.getHeight(null), leftWallWidth, leftWallImage.getHeight(null), this);
        }

        // Draw the right wall image
        int rightWallWidth = rightWallImage.getWidth(null);
        int numRightWallTiles = (int) Math.ceil((double) panelHeight / rightWallImage.getHeight(null)); 
        for (int i = 0; i < numRightWallTiles; i++) {
            g.drawImage(rightWallImage, getWidth() - rightWallWidth, i * rightWallImage.getHeight(null), rightWallWidth, rightWallImage.getHeight(null), this);
        }

        // Draw the lava surface (top row) by tiling the surface image horizontally
        int surfaceWidth = lavaSurfaceImage.getWidth(null);
        int surfaceHeight = lavaSurfaceImage.getHeight(null);
        int numTiles = getWidth() / surfaceWidth + 1; 
        for (int i = 0; i < numTiles; i++) {
            g.drawImage(lavaSurfaceImage, i * surfaceWidth, lavaTop, surfaceWidth, surfaceHeight, this);
        }

        // Draw the lava body (bottom and body)
        int lavaBodyY = lavaTop + surfaceHeight;
        int lavaBodyHeight = panelHeight - lavaBodyY;
        g.drawImage(lavaBodyImage, 0, lavaBodyY, getWidth(), lavaBodyHeight, this);

        // Shift JPanel upwards if player reaches the top
        if (player.getY() < panelHeight / 4) {
            lavaTop += 3; 
        }
    }

    
}
