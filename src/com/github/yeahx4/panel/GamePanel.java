package com.github.yeahx4.panel;

import com.github.yeahx4.constant.Const;
import com.github.yeahx4.constant.Point;
import com.github.yeahx4.constant.SnakeDriection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

  final Random random;
  final Point apple;
  final Point[] pos = new Point[Const.GAME_UNITS];
  int bodyParts = 6;
  int applesEaten = 0;
  SnakeDriection direction = SnakeDriection.RIGHT;
  boolean running = false;
  Timer timer;

  public GamePanel() {
    random = new Random();
    this.setPreferredSize(new Dimension(Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT));
    this.setBackground(Color.BLACK);
    this.setFocusable(true);
    this.addKeyListener(new MyKeyAdapter());
    this.apple = new Point(0, 0);
    startGame();
  }

  public void startGame() {
    for (int i = 0; i < pos.length; i++) {
      pos[i] = new Point();
    }

    newApple();
    running = true;
    timer = new Timer(Const.DELAY, this);
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
      for (int i = 0; i < Const.SCREEN_HEIGHT / Const.UNIT_SIZE; i++) {
        g.drawLine(i * Const.UNIT_SIZE, 0, i * Const.UNIT_SIZE, Const.SCREEN_HEIGHT);
        g.drawLine(0, i * Const.UNIT_SIZE, Const.SCREEN_WIDTH, i * Const.UNIT_SIZE);
      }

      // Draw Apple
      g.setColor(Color.RED);
      g.fillOval(apple.x, apple.y, Const.UNIT_SIZE, Const.UNIT_SIZE);

      // Draw Snake
      for (int i = 0; i < bodyParts; i++) {
        if (i == 0) {
          g.setColor(Color.GREEN);
        } else {
          g.setColor(new Color(45, 180, 0));
        }
        g.fillRect(pos[i].x, pos[i].y, Const.UNIT_SIZE, Const.UNIT_SIZE);
      }
      drawScore(g);
    } else {
      gameOver(g);
    }
  }

  public void newApple() {
    apple.x = random.nextInt(Const.SCREEN_WIDTH / Const.UNIT_SIZE) * Const.UNIT_SIZE;
    apple.y = random.nextInt(Const.SCREEN_HEIGHT / Const.UNIT_SIZE) * Const.UNIT_SIZE;
  }

  public void move() {
    for (int i = bodyParts; i > 0; i--) {
      pos[i].x = pos[i - 1].x;
      pos[i].y = pos[i - 1].y;
    }

    switch (direction) {
      case UP -> pos[0].y -= Const.UNIT_SIZE;
      case DOWN -> pos[0].y += Const.UNIT_SIZE;
      case LEFT -> pos[0].x -= Const.UNIT_SIZE;
      case RIGHT -> pos[0].x += Const.UNIT_SIZE;
    }
  }

  public void checkApple() {
    if ((pos[0].x == apple.x) && pos[0].y == apple.y) {
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
    if (pos[0].x > Const.SCREEN_WIDTH) {
      running = false;
    }
    // Head touches top border
    if (pos[0].y < 0) {
      running = false;
    }
    // Head touches bottom border
    if (pos[0].y > Const.SCREEN_HEIGHT) {
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
    int stringX = (Const.SCREEN_WIDTH - metrics1.stringWidth(str)) / 2;
    g.drawString(str, stringX, g.getFont().getSize());
  }

  public void gameOver(Graphics g) {
    drawScore(g);

    // Game Over text
    g.setColor(Color.RED);
    g.setFont(new Font("Ink Free", Font.BOLD, 75));
    FontMetrics metrics = getFontMetrics(g.getFont());

    String str = "Game Over";
    int stringX = (Const.SCREEN_WIDTH - metrics.stringWidth(str)) / 2;
    g.drawString(str, stringX, Const.SCREEN_HEIGHT / 2);
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
          if (direction != SnakeDriection.RIGHT)
            direction = SnakeDriection.LEFT;
          break;
        case KeyEvent.VK_RIGHT:
          if (direction != SnakeDriection.LEFT)
            direction = SnakeDriection.RIGHT;
          break;
        case KeyEvent.VK_UP:
          if (direction != SnakeDriection.DOWN)
            direction = SnakeDriection.UP;
          break;
        case KeyEvent.VK_DOWN:
          if (direction != SnakeDriection.UP)
            direction = SnakeDriection.DOWN;
          break;
      }
    }
  }
}
