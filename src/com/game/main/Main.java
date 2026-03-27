package com.game.main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Type Defense - OOP Project");
        GamePanel panel = new GamePanel();
        
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // กลางหน้าจอ
        frame.setVisible(true);
    }
}