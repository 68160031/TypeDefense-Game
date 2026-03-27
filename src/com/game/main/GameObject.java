package com.game.main;

import java.awt.Graphics;

public abstract class GameObject {
    // Encapsulation: ใช้ protected เพื่อให้ Class ลูกเรียกใช้ได้โดยตรง
    protected int x, y;
    protected int speed;

    public GameObject(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    // Abstract methods: บังคับให้ลูกๆ ต้องมี 2 ฟังก์ชันนี้
    public abstract void update();
    public abstract void draw(Graphics g);
    
    // Getter สำหรับเช็คตำแหน่ง Y (เอาไว้เช็คว่าหลุดขอบจอหรือยัง)
    public int getY() { return y; }
}