/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videogame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author charles
 */
public class Game implements Runnable {

    /**
     * start main game thread
     */
    @Override
    public void run() {
        init();

        int fps = 50;
        double timeTick = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();

        while (running) {
            now = System.nanoTime();
            delta += (now - lastTime) / timeTick;
            lastTime = now;

            if (delta >= 1) {
                tick();
                render();
                delta--;
            }
        }

        stop();
    }

    private BufferStrategy bs;
    private Graphics g;
    private Display display;

    String title;
    private int width;
    private int height;

    private Thread thread;
    private boolean running;

    private KeyManager keyManager;
    private MouseManager mouseManager;

    private Player player;
    private ArrayList<Brick> bricks;

    private Ball ball;
    
    private boolean pause;
    private boolean gameOver;
    
    private boolean brickBroke;
    private int numBrokenBricks;
    
    private boolean starting;
    
    private int score;
    private int combo;

    /**
     * to create title, width and heigh t and set the game is still not running
     * 
     * @param title  to set the title of the window
     * @param width  to set the width of the window
     * @param height to set the height of the window
     */
    public Game(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        bricks = new ArrayList<>();
        running = false;
        keyManager = new KeyManager();
        mouseManager = new MouseManager();
        pause = false;
        starting = true;
        score = 0;
        combo = 1;
    }

    /**
     * initializing the display window of the game
     */
    private void init() {
        display = new Display(title, width, height);
        Assets.init();

        player = new Player(getWidth() / 2, 650, 150, 30, this);
        ball = new Ball(getWidth() / 2 - 25, getHeight() / 2 - 25, 50, 50, this);
        
        int brickNum = 0;
        int row = 0;
        for (int i = 0; i < 24; i++) {
            if(brickNum >= 6){
                brickNum = 0;
                row++;
            }
            bricks.add(new Brick(100+brickNum*173, 100+row*75, 100, 50, this));
            brickNum++;
        }
        brickBroke = false;
        numBrokenBricks = 0;

        display.getJframe().addKeyListener(keyManager);
        display.getJframe().addMouseListener(mouseManager);
        display.getJframe().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().addMouseMotionListener(mouseManager);
        
        resetPositions();
    }

    /**
     * updates all objects on a frame
     */
    private void tick() {
        if (gameOver) {
            keyManager.tick();
            if (keyManager.r) {
                gameOver = false;
                resetGame();
            }
            return;
        }
        
        if (pause) {
            keyManager.tick();
            if (keyManager.g)
                saveGame();
            if (keyManager.c)
                loadGame();
            if (keyManager.p) {
                pause = !pause;
            }
            return;
        }
        
        keyManager.tick();
        player.tick();
        ball.tick();
        
        if (starting) {
            if (keyManager.space) {
                starting = false;
            }
        }

        if (ball.intersects(player) && !starting) {
            combo = 1;
            
            //Check if ball hits from up
            if (ball.getY() + ball.getHeight() <= player.getY() + 10) { //TODO: Check this condition
                
                //Calculate the distance the ball is from the center of the player pad
                float dist = ball.getX() + ball.getWidth() / 2 - (player.getX() + player.getWidth() / 2);
                
                //Map the dist value in proportion to the maxVel of the ball
                int deltaVel = (int) ((dist / 150.0) * ball.getMaxVel());
                
                //Update ball's xVel accordingly
                ball.setxVel(ball.getxVel() + deltaVel);
            }
            
            //Bounce the velocity in the y component
            ball.setyVel(ball.getyVel() * -1);
        }
        
        for(int i=0; i<bricks.size(); i++){
            Brick myBrick = bricks.get(i);
            myBrick.tick();
            if(ball.intersects(myBrick) && !brickBroke && !myBrick.isBroken()){
                score += 100*combo;
                combo++;
                bricks.get(i).setBroken(true);
                numBrokenBricks++;
                brickBroke = true;
                bricks.get(i).setBroken(true);
                
                bricks.get(i).setRecentBroken(true);
                
                boolean brickBetween = false;
                boolean upBetween = false;
                boolean downBetween = false;
                int padding = 5;
                if(ball.getY()>myBrick.getY()+padding&&ball.getY()<myBrick.getY()+myBrick.getHeight()-padding){
                    upBetween = true;
                }
                else if(ball.getY()+ball.getHeight()>myBrick.getY()+padding&&ball.getY()+ball.getHeight()<myBrick.getY()+myBrick.getHeight()-padding){
                    downBetween = true;
                }
                else if(ball.getY()<myBrick.getY()&&ball.getY()+ball.getHeight()>myBrick.getY()+myBrick.getHeight()){
                    brickBetween = true;
                }
                
                if(upBetween||downBetween||brickBetween){
                    ball.setxVel(ball.getxVel() * -1);
                }
                else{
                    ball.setyVel(ball.getyVel() * -1);               

                }
            }
        }
        
        brickBroke = false;
        if (ball.getY() + ball.getHeight() > getHeight()) {
            player.setLives(player.getLives()-1);
            resetPositions();
        }
        
        if (player.getLives() <= 0) {
            gameOver = true;
        }
        
        if (keyManager.p) {
            pause = !pause;
        }
    }

    /**
     * renders all objects in a frame
     */
    private void render() {
        Toolkit.getDefaultToolkit().sync(); // Linux
        bs = display.getCanvas().getBufferStrategy();

        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
        } else {
            g = bs.getDrawGraphics();
            g.clearRect(0, 0, width, height);
            g.drawImage(Assets.background, 0, 0, width, height, null);
            player.render(g);
            ball.render(g);
            for (int i = 0; i < bricks.size(); i++) {
                Brick myBrick = bricks.get(i);
                myBrick.render(g);
            }
            
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
            g.setColor(Color.white);
            g.drawString("Score: " + Integer.toString(score), 40, 50);
            if(combo>1){
                g.drawString("Combo: x" + Integer.toString(combo), 40, 80);

            }
            
            for (int i = 0; i < player.getLives(); i++) {
                g.drawImage(Assets.life, 250 + 40*i, 20, 40, 40, null);
            }
            
            if (gameOver) {
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
                g.drawString("GAME OVER", width/2 - 350, height/2 + 50);
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                g.drawString("Presiona R para iniciar un nuevo juego", width/2 - 300, height/2 + 100);

            }
            
            if (pause) {
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
                g.drawString("PAUSA", width/2 - 200, height/2 + 50);
            }

            bs.show();
            g.dispose();
        }
    }
    
    /**
     * Reset all game attributes to start a new game
     */
    void resetGame() {
        score = 0;
        combo = 1;
        for (int i = 0; i < bricks.size(); i++) {
            Brick myBrick = bricks.get(i);
            myBrick.setBroken(false);
            myBrick.setRecentBroken(false);
        }
        
        player.setLives(3);
        resetPositions();
    }

    /**
     * Reset movable objects to initial positions
     */
    void resetPositions() { 
        starting = true;
        combo = 1;
        ball.setxVel(0);
        player.setX(getWidth() / 2 - player.getWidth() / 2);
    }
    
    private void saveGame() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("game.txt"));
            
            pw.println(Integer.toString(player.getLives()));
            pw.println(Integer.toString(player.getX()));
            pw.println(Integer.toString(player.getY()));
            pw.println(Integer.toString(ball.getX()));
            pw.println(Integer.toString(ball.getY()));
            pw.println(Integer.toString(ball.getxVel()));
            pw.println(Integer.toString(ball.getyVel()));
            
            for (int i = 0; i < bricks.size(); i++) {
                Brick myBrick = bricks.get(i);
                pw.println(Integer.toString(myBrick.isBroken() ? 0 : 1));
            }
            pw.println(Integer.toString(score));
            pw.close();
            System.out.println("SAVING...");

        } catch(IOException e) {
            System.out.println("BEEP BEEP");
            System.out.println(e.toString());
        }
    }
    
    private void loadGame() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("game.txt"));
            player.setLives(Integer.parseInt(br.readLine()));
            player.setX(Integer.parseInt(br.readLine()));
            player.setY(Integer.parseInt(br.readLine()));
            ball.setX(Integer.parseInt(br.readLine()));
            ball.setY(Integer.parseInt(br.readLine()));
            ball.setxVel(Integer.parseInt(br.readLine()));
            ball.setyVel(Integer.parseInt(br.readLine()));
            
            for (int i = 0; i < bricks.size(); i++) {
                Brick myBrick = bricks.get(i);
                myBrick.setBroken(Integer.parseInt(br.readLine()) == 0);
            }
            
            score = Integer.parseInt(br.readLine());
            
        } catch (IOException e) {
            System.out.println("BEEP BEEP");
            System.out.println(e.toString());
        }
    }

    /**
     * to get width
     * 
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * to get height
     * 
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * to get key manager
     * 
     * @return keyManager
     */
    public KeyManager getKeyManager() {
        return keyManager;
    }

    /**
     * to get mouse manager
     * 
     * @return mouseManager
     */
    public MouseManager getMouseManager() {
        return mouseManager;
    }
    
    public boolean getStarting() {
        return starting;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * start game
     */
    public synchronized void start() {
        if (!running) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * stop game
     */
    public synchronized void stop() {
        if (running) {
            running = false;
            try {
                thread.join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
