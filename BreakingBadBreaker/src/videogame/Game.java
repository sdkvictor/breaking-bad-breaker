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
    private ArrayList<Brick> bricks;
    private LinkedList<PowerUp> powerups;

    private Ball ball;
    
    private boolean pause;
    private boolean gameOver;
    private boolean gameDone;
    
    private boolean brickBroke;
    private int numBrokenBricks;
    
    private boolean starting;
    
    private int score;
    private int combo;
    private int maxCombo;

    private boolean gameStart;
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
        powerups = new LinkedList<>();
        running = false;
        keyManager = new KeyManager();
        mouseManager = new MouseManager();
        pause = false;
        starting = true;
        score = 0;
        combo = 0;
        gameDone = false;
        maxCombo = 0;
        gameStart = false;
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
        
        Assets.backMusic.setLooping(true);
        Assets.backMusic.play();
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
        
        if (gameDone) {
            keyManager.tick();
            if (keyManager.r) {
                gameDone = false;
                resetGame();
            }
            return;
        }
        
        keyManager.tick();
        
        if(!gameStart){
            if(keyManager.enter){
                gameStart = true;
            }
        }
        if(gameStart){
            player.tick();
            ball.tick();

            if (starting) {
                if (keyManager.space) {
                    starting = false;
                }
            }

            if (ball.intersects(player) && !starting) {
                combo = 0;
                int pad = 10;

                
                boolean playerBetween = ball.getY() > player.getY() + pad && ball.getY() < player.getY() + player.getHeight() - pad;
                boolean upBetweenPlayer = ball.getY() + ball.getHeight() > player.getY() + pad&&ball.getY() + ball.getHeight() < player.getY()
                            + player.getHeight() - pad;
                boolean downBetweenPlayer = ball.getY() < player.getY() && ball.getY() + ball.getHeight() > player.getY() + player.getHeight();
                //Check if the ball hits from aside
                if(playerBetween || upBetweenPlayer || downBetweenPlayer){
                    if(ball.getX()>player.getX()+player.getWidth()-10){
                        //the ball hits the player from the right
                        //make x vel positive
                        ball.setxVel(Math.abs(ball.getxVel()));
                    }
                    else{ //the ball hits the player from the left
                        //make x vel negative
                        ball.setxVel(Math.abs(ball.getxVel())*-1);
                    }
                }
                
                //if(ball.getLastPos()>player.getY()-pad)
                else{  //ball hits from up
                    //Calculate the distance the ball is from the center of the player pad
                    float dist = ball.getX() + ball.getWidth() / 2 - (player.getX() + player.getWidth() / 2);

                    //Map the dist value in proportion to the maxVel of the ball
                    int deltaVel = (int) ((dist / 150.0) * ball.getMaxVel());

                    //Update ball's xVel accordingly
                    ball.setxVel(ball.getxVel() + deltaVel);

                    //Bounce the velocity in the y component
                    ball.setyVel(Math.abs(ball.getyVel())*(-1));
                }
                // if (ball.getY() + ball.getHeight() <= player.getY() + 10) { //TODO: Check this condition
                //}
            }

            boolean bricksDone = true;

            //Tick for all bricks
            for(int i=0; i<bricks.size(); i++){
                Brick myBrick = bricks.get(i);
                myBrick.tick();

                bricksDone = bricksDone && myBrick.isBroken();

                //Check if the ball collides with a brick
                if(ball.intersects(myBrick) && !brickBroke && !myBrick.isBroken()){
                    combo++;
                    if(combo>maxCombo){
                        maxCombo = combo;
                    }

                    myBrick.setLives(myBrick.getLives()-1);

                    if (myBrick.getLives() == 0) {
                        //Indicate that the brick has recently been broken
                        myBrick.setRecentBroken(true);
                        //Set it to permanently broken
                        myBrick.setBroken(true);
                        score += 200*combo;
                        Assets.explosionSound.play();
                    } else {
                        score += 50*combo;
                    }


                    //Decide if create a powerup, chance is 1/2
                    boolean createPower = ((int) (Math.random() * 2)) == 0;

                    if (createPower) {
                        //Choose a random power
                        int power = (int) (Math.random() * 2);
                        powerups.add(new PowerUp(myBrick.getX(), myBrick.getY(), 60, 20, power));
                    }

                    numBrokenBricks++;
                    brickBroke = true;

                    //Padding is the error tolerance of the collision of the ball with the left and right side of the bricks
                    int padding = 5;

                    boolean brickBetween = ball.getY() > myBrick.getY() + padding && ball.getY() < myBrick.getY() + myBrick.getHeight() - padding;
                    boolean upBetween = ball.getY() + ball.getHeight() > myBrick.getY() + padding&&ball.getY() + ball.getHeight() < myBrick.getY()
                            + myBrick.getHeight() - padding;
                    boolean downBetween = ball.getY() < myBrick.getY() && ball.getY() + ball.getHeight() > myBrick.getY() + myBrick.getHeight();

                    //Check if the collision is on the side of the brick
                    if(upBetween || downBetween || brickBetween) {
                        //If so invert the x speed of the ball
                        ball.setxVel(ball.getxVel() * -1);
                    }
                    else{
                        //Else invert the y speed of the ball
                        ball.setyVel(ball.getyVel() * -1);               
                    }
                }
            }

            //Tick for all powerups
            for (int i = 0; i < powerups.size(); i++) {
                PowerUp powerup = powerups.get(i);

                powerup.tick();

                if (powerup.intersects(player)) {
                    powerups.remove(i);

                    //Check which power is activated
                    switch(powerup.power) {
                        case speed:
                            player.activateFastSpeed();
                            break;

                        case size:
                            player.activateBigSize();
                            break;
                    }
                }
            }

            gameDone = bricksDone;

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
            if(!gameStart){
                g = bs.getDrawGraphics();
                g.clearRect(0, 0, width, height);
                g.drawImage(Assets.startScreen, 0, 0, width, height, null);
            }
            else{
                g = bs.getDrawGraphics();
                g.clearRect(0, 0, width, height);
                g.drawImage(Assets.background, 0, 0, width, height, null);
                player.render(g);
                ball.render(g);
                for (int i = 0; i < bricks.size(); i++) {
                    Brick myBrick = bricks.get(i);
                    myBrick.render(g);
                }

                for (int i = 0; i < powerups.size(); i++) {
                    PowerUp powerup = powerups.get(i);
                    powerup.render(g);
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
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Final Score: " + score, width/2 - 120, height/2 + 150);
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Maximum Combo: " + maxCombo, width/2 - 152, height/2 + 200);
                }

                if (pause) {
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
                    g.drawString("PAUSA", width/2 - 200, height/2 + 50);
                }

                if (gameDone) {
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
                    g.drawString("YOU WIN!", width/2 - 250, height/2 + 50);
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Presiona R para iniciar un nuevo juego", width/2 - 300, height/2 + 100);
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Final Score: " + score, width/2 - 120, height/2 + 150);
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Maximum Combo: " + maxCombo, width/2 - 152, height/2 + 200);
                }

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
            myBrick.setLives(2);
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
            
            if (player.isIsSpeedPower()) {
                pw.println(Integer.toString(1));
                pw.println(Integer.toString(player.getSpeedCounter()));
            } else {
                pw.println(Integer.toString(0));
                pw.println(Integer.toString(0));
            }
            
            if (player.isIsSizePower()) {
                pw.println(Integer.toString(1));
                pw.println(Integer.toString(player.getSizeCounter()));
            } else {
                pw.println(Integer.toString(0));
                pw.println(Integer.toString(0));
            }
            
            for (int i = 0; i < bricks.size(); i++) {
                Brick myBrick = bricks.get(i);
                pw.println(Integer.toString(myBrick.getLives()));
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
            
            player.setIsSpeedPower(Integer.parseInt(br.readLine()) == 1);
            
            if (player.isIsSpeedPower()) {
                player.activateFastSpeed();
            }
            
            player.setSpeedCounter(Integer.parseInt(br.readLine()));
            
            player.setIsSizePower(Integer.parseInt(br.readLine()) == 1);
            
            if (player.isIsSizePower()) {
                player.activateFastSpeed();
            }
            
            player.setSizeCounter(Integer.parseInt(br.readLine()));
            
            for (int i = 0; i < bricks.size(); i++) {
                Brick myBrick = bricks.get(i);
                myBrick.setLives(Integer.parseInt(br.readLine()));
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
