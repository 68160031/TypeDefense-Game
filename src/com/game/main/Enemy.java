package com.game.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random; // อย่าลืม import Random เพิ่มครับ

public class Enemy extends GameObject {
    private String word;
    private int currentIndex = 0; 
    private Color wordColor; // <--- เพิ่มตัวแปรเก็บสีสุ่มประจำตัว

    public int getX() { return x; }
    public int getY() { return y; }

    public Enemy(int x, int y, int speed, String word) {
        super(x, y, speed);
        this.word = word;
        
        // --- ส่วนที่เพิ่ม: สุ่มสีสดๆ ตอนสร้างศัตรู ---
        Random rand = new Random();
        // สุ่มค่า RGB 120-255 เพื่อให้ได้สีที่สว่าง (เห็นชัดบนฉากลาวามืดๆ)
        int r = rand.nextInt(136) + 120;
        int g = rand.nextInt(136) + 120;
        int b = rand.nextInt(136) + 120;
        this.wordColor = new Color(r, g, b);
    }

    @Override
    public void update() {
        y += speed;
    }

    @Override
    public void draw(Graphics g) {
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        
        // 1. วาด "เงาสีดำ" (ช่วยให้ตัวหนังสือทุกสีลอยเด่นขึ้นมา)
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(word, x + 2, y + 2);

        // 2. วาด "แสงเรือง (Glow)" เล็กน้อยรอบๆ ตัวหนังสือ
        g.setColor(new Color(wordColor.getRed(), wordColor.getGreen(), wordColor.getBlue(), 70));
        g.drawString(word, x - 1, y - 1);
        g.drawString(word, x + 1, y + 1);

        // 3. วาดคำศัพท์ด้วย "สีที่สุ่มได้" (จากเดิมที่เป็นสีขาว)
        g.setColor(wordColor);
        g.drawString(word, x, y);

        // 4. วาดตัวอักษรที่ "พิมพ์ถูกแล้ว" ทับลงไป (สีเหลืองสว่าง)
        if (currentIndex > 0) {
            g.setColor(Color.YELLOW);
            // เพิ่มเงาให้ตัวสีเหลืองด้วยเพื่อให้เห็นชัดตอนพิมพ์
            g.drawString(word.substring(0, currentIndex), x, y);
        }
    }

    public boolean checkInput(char c) {
        if (currentIndex < word.length() && word.charAt(currentIndex) == Character.toUpperCase(c)) {
            currentIndex++;
            return true;
        }
        return false;
    }

    public boolean isDead() {
        return currentIndex >= word.length();
    }
}