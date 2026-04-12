import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

 class PaddleGame extends JPanel implements ActionListener {
    // Game constants
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 15;
    private static final int BALL_SIZE = 15;
    private static final int PADDLE_SPEED = 8;
    private static final double BALL_SPEED = 5.0;
    
    // Game objects
    private int paddleX;
    private double ballX, ballY;
    private double ballVelX, ballVelY;
    private int score;
    private boolean gameOver;
    
    // Input handling
    private Set<Integer> keysPressed = new HashSet<>();
    
    // Game loop
    private Timer timer;
    
    public PaddleGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(20, 20, 30));
        setFocusable(true);
        
        // Initialize game state
        resetGame();
        
        // Keyboard input
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keysPressed.add(e.getKeyCode());
                if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    resetGame();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                keysPressed.remove(e.getKeyCode());
            }
        });
        
        // Game loop at 60 FPS
        timer = new Timer(1000 / 60, this);
        timer.start();
    }
    
    private void resetGame() {
        paddleX = WIDTH / 2 - PADDLE_WIDTH / 2;
        ballX = WIDTH / 2;
        ballY = HEIGHT / 2;
        
        // Random initial ball direction
        double angle = Math.random() * Math.PI / 2 - Math.PI / 4 + Math.PI / 2;
        ballVelX = Math.cos(angle) * BALL_SPEED;
        ballVelY = Math.sin(angle) * BALL_SPEED;
        
        score = 0;
        gameOver = false;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            updateGame();
        }
        repaint();
    }
    
    private void updateGame() {
        // Handle paddle movement
        if (keysPressed.contains(KeyEvent.VK_LEFT) || keysPressed.contains(KeyEvent.VK_A)) {
            paddleX -= PADDLE_SPEED;
        }
        if (keysPressed.contains(KeyEvent.VK_RIGHT) || keysPressed.contains(KeyEvent.VK_D)) {
            paddleX += PADDLE_SPEED;
        }
        
        // Keep paddle in bounds
        paddleX = Math.max(0, Math.min(WIDTH - PADDLE_WIDTH, paddleX));
        
        // Update ball position
        ballX += ballVelX;
        ballY += ballVelY;
        
        // Ball collision with walls
        if (ballX <= 0 || ballX >= WIDTH - BALL_SIZE) {
            ballVelX = -ballVelX;
            ballX = Math.max(0, Math.min(WIDTH - BALL_SIZE, ballX));
        }
        
        // Ball collision with top
        if (ballY <= 0) {
            ballVelY = -ballVelY;
            ballY = 0;
        }
        
        // Ball collision with paddle
        int paddleY = HEIGHT - 50;
        if (ballY + BALL_SIZE >= paddleY && ballY <= paddleY + PADDLE_HEIGHT &&
            ballX + BALL_SIZE >= paddleX && ballX <= paddleX + PADDLE_WIDTH) {
            
            // Bounce ball
            ballVelY = -Math.abs(ballVelY);
            ballY = paddleY - BALL_SIZE;
            
            // Add horizontal velocity based on hit position
            double hitPos = (ballX + BALL_SIZE / 2 - paddleX) / PADDLE_WIDTH;
            ballVelX = (hitPos - 0.5) * BALL_SPEED * 1.5;
            
            // Increase speed slightly
            double speedMultiplier = 1.02;
            ballVelX *= speedMultiplier;
            ballVelY *= speedMultiplier;
            
            score++;
        }
        
        // Game over condition
        if (ballY >= HEIGHT) {
            gameOver = true;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (!gameOver) {
            // Draw paddle
            g2d.setColor(new Color(100, 200, 255));
            g2d.fillRoundRect(paddleX, HEIGHT - 50, PADDLE_WIDTH, PADDLE_HEIGHT, 8, 8);
            
            // Draw ball
            g2d.setColor(new Color(255, 255, 100));
            g2d.fillOval((int)ballX, (int)ballY, BALL_SIZE, BALL_SIZE);
            
            // Draw score
            g2d.setColor(new Color(180, 180, 200));
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("Score: " + score, 20, 40);
            
            // Draw controls hint
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("Use Arrow Keys or A/D", WIDTH - 180, 30);
            
        } else {
            // Game over screen
            g2d.setColor(new Color(255, 100, 100));
            g2d.setFont(new Font("Arial", Font.BOLD, 48));
            String gameOverText = "Game Over";
            int textWidth = g2d.getFontMetrics().stringWidth(gameOverText);
            g2d.drawString(gameOverText, WIDTH / 2 - textWidth / 2, HEIGHT / 2 - 40);
            
            g2d.setColor(new Color(200, 200, 200));
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            String scoreText = "Final Score: " + score;
            textWidth = g2d.getFontMetrics().stringWidth(scoreText);
            g2d.drawString(scoreText, WIDTH / 2 - textWidth / 2, HEIGHT / 2 + 20);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            String restartText = "Press SPACE to restart";
            textWidth = g2d.getFontMetrics().stringWidth(restartText);
            g2d.drawString(restartText, WIDTH / 2 - textWidth / 2, HEIGHT / 2 + 70);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Paddle Game");
            PaddleGame game = new PaddleGame();
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
            game.requestFocusInWindow();
        });
    }
}