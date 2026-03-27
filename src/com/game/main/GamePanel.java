package com.game.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;


public class GamePanel extends JPanel implements ActionListener {
	private Image backgroundImage;
	private ArrayList<String> wordList = new ArrayList<>();
    private Timer timer;
    private ArrayList<Enemy> enemies;
    private Random random = new Random();
    private int score = 0;
    private int lives = 3;           // จำนวนชีวิตเริ่มต้น
    private boolean isGameOver = false; // สถานะเกม (true = จบเกม, false = กำลังเล่น)
    private int combo = 0;       // จำนวนคำที่พิมพ์ถูกต่อเนื่อง
    private int level = 1;       // ระดับความยากปัจจุบัน
    private int enemySpeed = 2;  // ความเร็วเริ่มต้นของศัตรู
    private ArrayList<Particle> particles = new ArrayList<>();
    private boolean isWin = false; // สถานะชนะเกม
 // เพิ่มตัวแปร Enum หรือใช้ตัวเลขเพื่อบอกสถานะ
    private enum State { MENU, PLAYING, WIN, GAME_OVER }
    private State currentState = State.MENU; // เริ่มต้นที่หน้าเมนู
    private int shakeAmount = 0; // พลังการสั่น (ยิ่งเยอะยิ่งสั่นแรง)
    
    
    

    public GamePanel() {
    	loadWords();
    	backgroundImage = new ImageIcon("background.png").getImage();
        enemies = new ArrayList<>();
        
        // เริ่มต้นด้วยศัตรู 1 ตัว
        spawnEnemy();

        // Game Loop: ทำงานทุก 20 มิลลิวินาที (~50 FPS)
        timer = new Timer(20, this);
        timer.start();

        // รับค่าจากคีย์บอร์ด
        setFocusable(true);
     // ใน GamePanel.java (ส่วน Constructor)
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();

                // 1. เช็คปุ่ม Enter และปุ่ม R (เหมือนเดิม)
                if (currentState == State.MENU && key == '\n') {
                    restartGame();
                    currentState = State.PLAYING;
                    repaint();
                    return;
                }

                if ((currentState == State.GAME_OVER || currentState == State.WIN) 
                    && (key == 'r' || key == 'R')) {
                    restartGame();
                    currentState = State.PLAYING;
                    repaint();
                    return;
                }
                if ((currentState == State.GAME_OVER || currentState == State.WIN) 
                        && (key == 'e' || key == 'E')) {
                        System.exit(0); // คำสั่งปิดโปรแกรมทันที
                }

                // 2. ส่วนที่เช็คการพิมพ์คำศัพท์ (จุดที่ต้องเพิ่มการสั่น)
                if (currentState == State.PLAYING) {
                    boolean typedCorrect = false; // ตัวแปรไว้เช็คว่า "รอบนี้พิมพ์ถูกไหม"

                    for (Enemy en : enemies) {
                        if (en.checkInput(key)) {
                            typedCorrect = true; // ถ้ามีศัตรูตัวไหนบอกว่า "ตัวนี้แหละที่ฉันรออยู่"
                            break; 
                        }
                    }

                    // --- ถ้าเช็คจนจบ Loop แล้วไม่มีตัวไหนถูกเลย (พิมพ์ผิด) ---
                    if (!typedCorrect) {
                        shakeAmount = 5; // สั่นเบาๆ ให้พอรู้ตัวว่าพิมพ์พลาด
                    }
                    
                    repaint();
                }
            }
        });
    }

    private void spawnEnemy() {
        if (wordList.isEmpty()) return; // กัน Error ถ้าไฟล์ว่าง

        // สุ่มคำจาก ArrayList ที่เราโหลดมาจากไฟล์
        String pickedWord = wordList.get(random.nextInt(wordList.size()));
        
        // สุ่มตำแหน่ง X (50 ถึง 700)
        int startX = random.nextInt(650) + 50;
        
        // สร้างศัตรู (ใช้ enemySpeed ที่เราทำไว้ในข้อที่แล้ว)
        enemies.add(new Enemy(startX, 0, enemySpeed, pickedWord));
    }
    
    private void createExplosion(int x, int y, Color color) {
        for (int i = 0; i < 15; i++) { // สร้างเศษซาก 15 ชิ้น
            particles.add(new Particle(x, y, color));
        }
    }
    
    
    
    private void levelUp() {
        level++;
        // เพิ่มความเร็วทีละน้อย (เช่น ทุกเลเวลเพิ่มความเร็ว 0.5 หรือ 1)
        if (level % 2 == 0) { 
            enemySpeed++; 
        }
        // ถ้าเลเวลสูงๆ อาจจะสั่งให้ spawnEnemy() เพิ่มอีกตัวพร้อมกันก็ได้
    }
    
    private void loadWords() {
        try {
            // อ่านไฟล์ words.txt
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("words.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    wordList.add(line.trim().toUpperCase());
                }
            }
            reader.close();
        } catch (java.io.IOException e) {
            // ถ้าหาไฟล์ไม่เจอ ให้ใช้คำพื้นฐานแทน (กันเหนียว)
            wordList.add("JAVA");
            wordList.add("OOP");
            System.out.println("Error: Could not find words.txt, using default words.");
        }
    }
    
 // ฟังก์ชันสำหรับรีเซ็ตค่าทุกอย่างให้กลับมาเริ่มต้นใหม่
    private void restartGame() {
        score = 0;
        lives = 3;
        combo = 0;
        level = 1;
        enemySpeed = 2;
        isGameOver = false;
        isWin = false; // <--- เพิ่มบรรทัดนี้
        enemies.clear();
        particles.clear();
        spawnEnemy();
        if (!timer.isRunning()) timer.start();
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        // --- ส่วนที่เพิ่มใหม่: คำนวณการสั่นหน้าจอ (ต้องทำก่อนวาดอย่างอื่น) ---
        if (shakeAmount > 0) {
            Random rand = new Random();
            // สุ่มค่า offset สำหรับการเลื่อนหน้าจอ
            int offsetX = rand.nextInt(shakeAmount) - shakeAmount / 2;
            int offsetY = rand.nextInt(shakeAmount) - shakeAmount / 2;
            
            // ย้ายจุดศูนย์กลางการวาดชั่วคราว
            g.translate(offsetX, offsetY); 
            
            // ค่อยๆ ลดความแรงของการสั่นลงทีละนิด
            shakeAmount--; 
        }

        // 1. ล้างหน้าจอเก่า (เรียก super หลังจาก translate เพื่อให้พื้นหลังที่ล้างถูกสั่นไปด้วย)
        super.paintComponent(g); 

        // --- 1. วาดพื้นหลัง ---
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // --- 2. แยกการวาดตามสถานะเกม ---
        if (currentState == State.MENU) {
            drawMenuScreen(g);
        } else {
            // --- ส่วนที่ 3: วาด HUD ขณะเล่นเกม ---
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(Color.YELLOW);
            g.drawString("Score: " + score, 20, 30);
            g.setColor(Color.RED);
            g.drawString("Lives: " + lives, 20, 60);
            g.setColor(Color.CYAN);
            g.drawString("LEVEL: " + level, 680, 30);
            
            if (combo > 0) {
                g.setColor(Color.ORANGE);
                g.setFont(new Font("Arial", Font.ITALIC, 25));
                g.drawString("COMBO X" + combo, 350, 40);
            }

            // --- ส่วนที่ 4: วาดศัตรูและเอฟเฟกต์ระเบิด ---
            for (Enemy en : enemies) {
                en.draw(g);
            }
            
            for (int i = 0; i < particles.size(); i++) {
                particles.get(i).draw(g);
            }

            // --- ส่วนที่ 5: วาดหน้าจอชนะ (YOU WIN!) ---
            if (currentState == State.WIN) {
                g.setColor(new Color(255, 255, 255, 150)); 
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setFont(new Font("Arial", Font.BOLD, 70));
                g.setColor(new Color(218, 165, 32)); 
                g.drawString("YOU WIN!", 230, 250);
                g.setFont(new Font("Arial", Font.PLAIN, 25));
                g.setColor(Color.BLACK);
                g.drawString("Final Score: " + score, 320, 300);
                g.drawString("Press 'R' to Play Again", 290, 350);
                g.drawString("Press 'E' to Exit", 320, 390);
            }

            // --- ส่วนที่ 6: วาดหน้าจอ Game Over ---
            if (currentState == State.GAME_OVER) {
                g.setColor(new Color(0, 0, 0, 180)); 
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 60));
                g.drawString("GAME OVER", 220, 250);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.setColor(Color.WHITE);
                g.drawString("Press 'R' to Restart", 310, 310);
                g.drawString("Press 'E' to Exit", 325, 350);
            }
        }
    }

    // เพิ่มเมธอดตัวช่วยสำหรับวาดหน้าเมนู
    private void drawMenuScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 160)); // พื้นหลังมืดๆ ให้ตัวหนังสือเด่น
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.setColor(Color.WHITE);
        g.drawString("TYPE DEFENSE", 180, 220);

        g.setFont(new Font("Arial", Font.PLAIN, 25));
        g.setColor(Color.YELLOW);
        g.drawString("Press 'ENTER' to Start", 260, 350);
        
        g.setFont(new Font("Arial", Font.ITALIC, 18));
        g.setColor(Color.LIGHT_GRAY);
        g.drawString("Developer: Pongpob Chan-um 68160031", 240, 500);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 1. เช็คสถานะเกม
        if (currentState != State.PLAYING) {
            updateParticles(); 
            repaint();
            return; 
        }

        // --- ส่วนที่ 1: อัปเดต Particle (เศษระเบิด) ---
        updateParticles();

        // --- ส่วนที่ 2: อัปเดตและเช็คสถานะศัตรู ---
        for (int i = 0; i < enemies.size(); i++) {
            Enemy en = enemies.get(i);
            en.update(); 

            // --- กรณีที่ 1: พิมพ์คำศัพท์จนครบ (กำจัดศัตรูได้) ---
            if (en.isDead()) {
                createExplosion(en.getX(), en.getY(), Color.YELLOW);
                
                // เพิ่มการสั่นหน้าจอแบบ "เบา" (ค่า 8-10 กำลังดี)
                shakeAmount = 10; 
                
                enemies.remove(i);
                score += (10 * level) + (combo * 2); 
                combo++; 
                
                if (score >= 1000) {
                    currentState = State.WIN;
                }
                
                if (combo % 5 == 0) {
                    levelUp();
                }
                
                if (currentState == State.PLAYING) {
                    spawnEnemy();
                }
                i--; 
            }            
            // --- กรณีที่ 2: ศัตรูหลุดขอบจอด้านล่าง (พิมพ์ไม่ทัน) ---
            else if (en.getY() > 600) {
                enemies.remove(i);
                lives--;
                
                // เพิ่มการสั่นหน้าจอแบบ "แรง" (ค่า 20-30 เพื่อให้รู้ว่าพลาดหนัก!)
                shakeAmount = 25; 
                
                combo = 0; 
                
                if (lives <= 0) {
                    currentState = State.GAME_OVER;
                } else {
                    spawnEnemy();
                }
                i--; 
            }
        }
        
        // 3. สั่งให้โปรแกรมวาดหน้าจอใหม่
        repaint();
    }

    // แยกเมธอดอัปเดต Particle ออกมาเพื่อให้โค้ดสะอาดขึ้น
    private void updateParticles() {
        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            p.update();
            if (p.isDead()) {
                particles.remove(i);
                i--; 
            }
        }
    }
}