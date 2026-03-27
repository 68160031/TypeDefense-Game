package com.game.main;

import java.awt.*;
import java.util.Random;

public class Particle {
    private double x, y;
    private double velX, velY; // ความเร็วในแนวแกน X และ Y
    private int life = 20;     // ระยะเวลาที่จะแสดงบนจอ (เฟรม)
    private Color color;

    public Particle(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        Random rand = new Random();
        // สุ่มทิศทางกระจายตัวออกไปรอบๆ
        this.velX = (rand.nextDouble() - 0.5) * 10;
        this.velY = (rand.nextDouble() - 0.5) * 10;
    }

    public void update() {
        x += velX;
        y += velY;
        life--; // ค่อยๆ จางหายไป
    }

    public void draw(Graphics g) {
        if (life > 0) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), life * 10)); 
            g.fillOval((int)x, (int)y, 8, 8); // วาดเป็นจุดเล็กๆ
        }
    }

    public boolean isDead() { return life <= 0; }
}