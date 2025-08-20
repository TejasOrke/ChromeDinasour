import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Chrome Dinosaur Game (Offline T-Rex Runner Clone)
 * -----------------------------------------------
 * A simple Java Swing game where the dinosaur jumps over cacti.
 * Uses JPanel, Timer, KeyListener, and collision detection.
 *
 * Author: Tejas (Cleaned & Labeled Version)
 */
public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {

    // ===================== Game Board Settings =====================
    int boardWidth = 750;
    int boardHeight = 250;

    // ===================== Images =====================
    Image dinosuarImage;
    Image dinosuarDeadImage;
    Image dinosuarJumpImage;
    Image cactus1Image;
    Image cactus2Image;
    Image cactus3Image;

    // ===================== Dinosaur Properties =====================
    int dinosaurWidth = 88;
    int dinosaurHeight = 94;
    int dinosaurX = 50;
    int dinosaurY = boardHeight - dinosaurHeight;
    Block dinosaur;

    // ===================== Cactus Properties =====================
    int cactus1Width = 34;
    int cactus2Width = 69;
    int cactus3Width = 102;
    int cactusHeight = 70;
    int cactusX = 700;
    int cactusY = boardHeight - cactusHeight;
    ArrayList<Block> cactusList;

    // ===================== Game Variables =====================
    boolean gameOver = false;
    int score = 0;

    // Movement (Physics)
    int velocityX = -12;   // Speed of cacti moving left
    int velocityY = 0;     // Vertical speed of dinosaur
    int gravity = 1;       // Gravity pulling dinosaur down

    // Timers
    Timer gameTimer;       // For game loop (60 FPS)
    Timer cactusTimer;     // For cactus spawning

    // ===================== Constructor =====================
    public ChromeDinosaur() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.lightGray);
        setFocusable(true);
        addKeyListener(this);

        // Load dinosaur images
        dinosuarImage = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
        dinosuarDeadImage = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
        dinosuarJumpImage = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();

        // Load cactus images
        cactus1Image = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
        cactus2Image = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
        cactus3Image = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();

        // Initialize dinosaur
        dinosaur = new Block(dinosaurX, dinosaurY, dinosaurWidth, dinosaurHeight, dinosuarImage);

        // Initialize cactus list
        cactusList = new ArrayList<>();

        // Game Timer (60 FPS refresh rate)
        gameTimer = new Timer(1000 / 60, this);
        gameTimer.start();

        // Cactus Spawner (every 1.5 seconds)
        cactusTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeCactus();
            }
        });
        cactusTimer.start();
    }

    // ===================== Main Game Loop =====================
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        // Stop everything if game over
        if (gameOver) {
            cactusTimer.stop();
            gameTimer.stop();
        }
    }

    // ===================== Movement & Physics =====================
    public void move() {
        // Apply gravity
        velocityY += gravity;
        dinosaur.y += velocityY;

        // Prevent dinosaur from falling below ground
        if (dinosaur.y > dinosaurY) {
            dinosaur.y = dinosaurY;
            velocityY = 0;
            dinosaur.image = dinosuarImage; // Switch back to run image
        }

        // Move cacti left
        for (int i = 0; i < cactusList.size(); i++) {
            Block cactus = cactusList.get(i);
            cactus.x += velocityX;

            // Check collision
            if (isCollision(dinosaur, cactus)) {
                gameOver = true;
                dinosaur.image = dinosuarDeadImage;
            }
        }

        // Increase score
        score++;
    }

    // ===================== Collision Detection =====================
    boolean isCollision(Block dino, Block cactus) {
        return dino.x < cactus.x + cactus.width &&
                dino.x + dino.width > cactus.x &&
                dino.y < cactus.y + cactus.height &&
                dino.y + dino.height > cactus.y;
    }

    // ===================== Keyboard Controls =====================
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Jump if on ground
            if (dinosaur.y == dinosaurY) {
                velocityY = -17;
                dinosaur.image = dinosuarJumpImage;
            }

            // Restart game after Game Over
            if (gameOver) {
                resetGame();
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    // ===================== Reset Game =====================
    private void resetGame() {
        dinosaur.y = dinosaurY;
        dinosaur.image = dinosuarImage;
        velocityY = 0;
        cactusList.clear();
        gameOver = false;
        score = 0;
        gameTimer.start();
        cactusTimer.start();
    }

    // ===================== Place New Cactus =====================
    public void placeCactus() {
        if (gameOver) return;

        double chance = Math.random();
        if (chance > 0.90) {
            cactusList.add(new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Image));
        } else if (chance > 0.70) {
            cactusList.add(new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Image));
        } else if (chance > 0.50) {
            cactusList.add(new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Image));
        }

        // Prevent too many cacti
        if (cactusList.size() > 10) {
            cactusList.remove(0);
        }
    }

    // ===================== Rendering =====================
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw dinosaur
        g.drawImage(dinosaur.image, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);

        // Draw cacti
        for (Block cactus : cactusList) {
            g.drawImage(cactus.image, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }

        // Draw Score
        g.setColor(Color.black);
        g.setFont(new Font("Courier New", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over : " + score, 10, 35);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    // ===================== Inner Block Class =====================
    class Block {
        int x, y, width, height;
        Image image;

        public Block(int x, int y, int width, int height, Image image) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
        }
    }
}
