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
import java.util.LinkedList;

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
    private LinkedList<Brick> bricks;

    private Ball ball;
    
    private boolean pause;
    private boolean gameOver;
    
    private boolean brickBroke;
    private int numBrokenBricks;
    
    private boolean starting;

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
        bricks = new LinkedList<Brick>();
        running = false;
        keyManager = new KeyManager();
        mouseManager = new MouseManager();
        pause = false;
        starting = true;
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
        for (int i = 0; i < 30; i++) {
            if(brickNum >= 6){
                brickNum = 0;
                row++;
            }
            bricks.add(new Brick(100+brickNum*154, 100+row*33, 150, 30, this));
            brickNum++;
        }
        brickBroke = false;
        numBrokenBricks = 0;

        display.getJframe().addKeyListener(keyManager);
        display.getJframe().addMouseListener(mouseManager);
        display.getJframe().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().addMouseMotionListener(mouseManager);

        setItemsPositions();
    }

    /**
     * updates all objects on a frame
     */
    private void tick() {
        if (pause) {
            keyManager.tick();
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

            int totalLength = ball.getWidth() + player.getWidth();
            
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
            if(ball.intersects(myBrick) && !brickBroke && !myBrick.isBroken()){
                bricks.get(i).setBroken(true);
                numBrokenBricks++;
                brickBroke = true;
                bricks.get(i).setBroken(true);
                ball.setyVel(ball.getyVel() * -1);               
            }
        }
        brickBroke = false;
        
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
            
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
            g.setColor(Color.white);
            
            if (pause) {
                g.drawString("PAUSA", width/2, height/2);
            }

            bs.show();
            g.dispose();

        }
    }

    /**
     * Initialize the positions of the current items
     */
    void setItemsPositions() {

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
