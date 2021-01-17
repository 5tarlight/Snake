package com.github.yeahx4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = SCREEN_WIDTH * SCREEN_HEIGHT / UNIT_SIZE;
    static final int DELAY = 75;
    final Point[] pos = new Point[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        for (int i = 0; i < pos.length; i++) {
            pos[i] = new Point();
        }

        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // Draw grid
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            // Draw Apple
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw Snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(pos[i].x, pos[i].y, UNIT_SIZE, UNIT_SIZE);
            }
            drawScore(g);
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            pos[i].x = pos[i - 1].x;
            pos[i].y = pos[i - 1].y;
        }

        switch (direction) {
            case 'U' -> pos[0].y -= UNIT_SIZE;
            case 'D' -> pos[0].y += UNIT_SIZE;
            case 'L' -> pos[0].x -= UNIT_SIZE;
            case 'R' -> pos[0].x += UNIT_SIZE;
        }
    }

    public void checkApple() {
        if ((pos[0].x == appleX) && pos[0].y == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // Head collides with the body
        for (int i = bodyParts; i > 0; i--) {
            if ((pos[0].x == pos[i].x) && (pos[0].y == pos[i].y)) {
                running = false;
                break;
            }
        }

        // Head touches left border
        if (pos[0].x < 0) {
            running = false;
        }
        // Head touches right border
        if (pos[0].x > SCREEN_WIDTH) {
            running = false;
        }
        // Head touches top border
        if (pos[0].y < 0) {
            running = false;
        }
        // Head touches bottom border
        if (pos[0].y > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void drawScore(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());

        String str = "Score: " + applesEaten;
        int stringX = (SCREEN_WIDTH - metrics1.stringWidth(str)) / 2;
        g.drawString(str, stringX, g.getFont().getSize());
    }

    public void gameOver(Graphics g) {
        drawScore(g);

        // Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());

        String str = "Game Over";
        int stringX = (SCREEN_WIDTH - metrics.stringWidth(str)) / 2;
        g.drawString(str, stringX, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R')
                        direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L')
                        direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D')
                        direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U')
                        direction = 'D';
                    break;
            }
        }
    }
}
