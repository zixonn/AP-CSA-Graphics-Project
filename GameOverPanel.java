import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameOverPanel extends JPanel {
    private long score;
    private GameScreen gameScreen; // Add a reference to the GameScreen instance

    public GameOverPanel(long score, GameScreen gameScreen) { // Accept GameScreen as a parameter
        this.score = score;
        this.gameScreen = gameScreen; // Assign the GameScreen instance

        setPreferredSize(new Dimension(300, 150));
        setLayout(new BorderLayout());

        // Display the game over message
        JLabel gameOverLabel = new JLabel("Game Over!");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(gameOverLabel, BorderLayout.NORTH);

        // Display the score
        JLabel scoreLabel = new JLabel("You survived for: " + score + " seconds");
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(scoreLabel, BorderLayout.CENTER);

        // Add restart button
        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 14));
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        add(restartButton, BorderLayout.SOUTH);
    }

    private void restartGame() {
        // Stop the music
        gameScreen.stopMusic();

        // Close the current game window
        Window window = SwingUtilities.getWindowAncestor(this);
        window.dispose();

        // Create a new game window
        myJFrame frame = new myJFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 700);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
