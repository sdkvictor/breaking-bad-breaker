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

    private KeyManager keyManager; //to manage key inputs
    private MouseManager mouseManager;//to use the mouse

    private Player player; //player's paddle
    private ArrayList<Brick> bricks; //array of bricks
    private LinkedList<PowerUp> powerups; //array of powerups
 
    private Ball ball;
    
    private boolean pause;
    private boolean gameOver; //whether the game is over
    private boolean gameDone;
    
    private boolean brickBroke;
    private int numBrokenBricks;
    
    private boolean starting;
    
    private int score; //current score
    private int combo; //current combo
    private int maxCombo; //maximum combo reached

    private boolean gameStart; //the game starts
    private boolean firstInstructions; //first instrutions screen
    private boolean pauseInstructions; //instructions from pause menu
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
        score = 0; //initialize score as 0
        combo = 0; //initialize combo as 0
        gameDone = false;
        maxCombo = 0; //initialize maxCombo as 0
        gameStart = false;
        firstInstructions = false;
        pauseInstructions = false;
    }

    /**
     * initializing the display window of the game
     */
    private void init() {
        display = new Display(title, width, height);
        Assets.init();

        player = new Player(getWidth() / 2, 650, 150, 30, this); //creates a player
        ball = new Ball(getWidth() / 2 - 25, getHeight() / 2 - 25, 50, 50, this); //creates a ball
        //makes the array of bricks to start the game
        int brickNum = 0;
        int row = 0;
        //24 total bricks
        for (int i = 0; i < 24; i++) {
            //6 bricks per row
            if(brickNum >= 6){
                brickNum = 0;
                row++;
            }
            //adds a brick to the array in the next position
            bricks.add(new Brick(100+brickNum*173, 100+row*75, 100, 50, this));
            //increase number of current bricks
            brickNum++;
        }
        //initialize all bricks as not broken
        brickBroke = false;
        numBrokenBricks = 0;

        display.getJframe().addKeyListener(keyManager);
        display.getJframe().addMouseListener(mouseManager);
        display.getJframe().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().addMouseMotionListener(mouseManager);
        
        //reset all the positions of the items
        resetPositions();
        
        Assets.backMusic.setLooping(true);
        Assets.backMusic.play();
    }

    /**
     * updates all objects on a frame
     */
    private void tick() {
        //if the game is over
        if (gameOver) {
            keyManager.tick();
            //if presses r
            if (keyManager.r) {
                gameOver = false;
                //starts a new game
                resetGame();
            }
            return;
        }
        
        //if the game is paused
        if (pause) {
            keyManager.tick();
            //if g key is pressed
            if (keyManager.g)
                //make a save file
                saveGame();
            //if c key is pressed
            if (keyManager.c)
                //load the save file
                loadGame();
            //if i key is pressed
            if  (keyManager.i){
                //set the pause instructions to true to be displayed
                pauseInstructions = true;
            }
            //if pause instructions are open
            if  (pauseInstructions){
                //if x key is pressed
                if(keyManager.x){
                    //set pause instructions to false to close
                    pauseInstructions = false;
                }
            }
            //if p key is pressed while pause instructions are closed
            if (keyManager.p && !pauseInstructions) {
                //change the status of pause to its opposite boolean value
                pause = !pause;
            }
            return;
        }
        //if the game is done
        if (gameDone) {
            keyManager.tick();
            if (keyManager.r) {
                //reset the game if r key is pressed
                gameDone = false;
                resetGame();
            }
            return;
        }
        
        keyManager.tick();
        //if the game hasn't started
        if(!gameStart){
            //if enter key is pressed
            if(keyManager.enter){
                //start the game
                gameStart = true;
            }
        }
        //if the first instructions haven't been closed
        if(!firstInstructions){
            if(keyManager.x){
                //close them if x is pressed
                firstInstructions = true;
            }
        }
        
        //if the game started and the first instructions were already seen
        if(gameStart&&firstInstructions){
            //tick player and ball items
            player.tick();
            ball.tick();
            //if the game just started and the ball is with the player
            if (starting) {
                if (keyManager.space) {
                    //start the game after pressing the space bar
                    starting = false;
                }
            }
            //if the player intersects with the ball and the game is not just starting
            if (ball.intersects(player) && !starting) {
                combo = 0; //reset the combo
                int pad = 10; //Padding is the error tolerance of the collision of the ball with the left and right side of the player
                //verify if the player is bewteen the upper and the lower edge of the ball
                boolean playerBetween = ball.getY() > player.getY() + pad && ball.getY() < player.getY() + player.getHeight() - pad;
                //verify if the ball's upper edge is bewteen the player's height
                boolean upBetweenPlayer = ball.getY() + ball.getHeight() > player.getY() + pad&&ball.getY() + ball.getHeight() < player.getY()
                            + player.getHeight() - pad;               
                //verify if the ball's lower edge is between the player's height
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
                    //increase the combo by 1
                    combo++;
                    //check if the max combo needs to be updated
                    if(combo>maxCombo){
                        maxCombo = combo;
                    }
                    //decreases one live out of the 2 lives of the brick 
                    myBrick.setLives(myBrick.getLives()-1);

                    if (myBrick.getLives() == 0) {
                        //Indicate that the brick has recently been broken
                        myBrick.setRecentBroken(true);
                        //Set it to permanently broken
                        myBrick.setBroken(true);
                        //increase the score by 200 times the current combo
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
                    //verify if the brick is bewteen the upper and the lower edge of the ball
                    boolean brickBetween = ball.getY() > myBrick.getY() + padding && ball.getY() < myBrick.getY() + myBrick.getHeight() - padding;
                    //verify if the ball's upper edge is bewteen the bricks's height
                    boolean upBetween = ball.getY() + ball.getHeight() > myBrick.getY() + padding&&ball.getY() + ball.getHeight() < myBrick.getY()
                            + myBrick.getHeight() - padding;
                    //verify if the ball's lower edge is bewteen the bricks's height
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
            //check if the player won the game yet
            gameDone = bricksDone;

            brickBroke = false;
            //take one life from the player if it reaches the ground and reset positions of the items
            if (ball.getY() + ball.getHeight() > getHeight()) {
                player.setLives(player.getLives()-1);
                resetPositions();
            }
            //game is over if the player loses all their lives
            if (player.getLives() <= 0) {
                gameOver = true;
            }
            //change the pause status if p key is pressed
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
            //if the game hasn't started yet and the start screen should be on
            if(!gameStart){
                g = bs.getDrawGraphics();
                g.clearRect(0, 0, width, height);
                //render the start screen
                g.drawImage(Assets.startScreen, 0, 0, width, height, null);
            }
            //if the first instructions haven't been seen yet
            else if(!firstInstructions){
                g = bs.getDrawGraphics();
                g.clearRect(0, 0, width, height);
                //render the instructions
                g.drawImage(Assets.instructions, 0, 0, width, height, null);
            }
            //if start screen and first instrutions have been closed
            else{
                g = bs.getDrawGraphics();
                g.clearRect(0, 0, width, height);
                //render the background, player and ball
                g.drawImage(Assets.background, 0, 0, width, height, null);
                player.render(g);
                ball.render(g);
                //render all the bricks
                for (int i = 0; i < bricks.size(); i++) {
                    Brick myBrick = bricks.get(i);
                    myBrick.render(g);
                }
                //render all the powerups
                for (int i = 0; i < powerups.size(); i++) {
                    PowerUp powerup = powerups.get(i);
                    powerup.render(g);
                }
                //Display the current score on the screen
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                g.setColor(Color.white);
                g.drawString("Score: " + Integer.toString(score), 40, 50);
                //if the combo is bigger than 1
                if(combo>1){
                    //display the combo count
                    g.drawString("Combo: x" + Integer.toString(combo), 40, 80);

                }
                //render all the remaining lives of the player
                for (int i = 0; i < player.getLives(); i++) {
                    g.drawImage(Assets.life, 250 + 40*i, 20, 40, 40, null);
                }
                //if instructions are opened in pause menu
                if(pauseInstructions){
                    //render the instructions
                    g.clearRect(0, 0, width, height);
                    g.drawImage(Assets.instructions, 0, 0, width, height, null);
                }
                //if all player lives were used
                if (gameOver) {
                    //display "game over"
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
                    g.drawString("GAME OVER", width/2 - 350, height/2 + 50);
                    //show instructions to restart the game
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Presiona R para iniciar un nuevo juego", width/2 - 300, height/2 + 100);
                    //show final score
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Final Score: " + score, width/2 - 120, height/2 + 150);
                    //show the maximum combo reached
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Maximum Combo: " + maxCombo, width/2 - 152, height/2 + 200);
                }
                //if pause menu is on and the instructions are closed
                if (pause&&!pauseInstructions) {
                    //show the message "PAUSA"
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
                    g.drawString("PAUSA", width/2 - 200, height/2 + 50);
                }
                //if all bricks were destroyed
                if (gameDone) {
                    //display "you win!" as the player won the game
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
                    g.drawString("YOU WIN!", width/2 - 250, height/2 + 50);
                    //show instructions to restart the game
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Presiona R para iniciar un nuevo juego", width/2 - 300, height/2 + 100);
                    //show final score and multiply it by the remaining lives of the player
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
                    g.drawString("Final Score: " + score*player.getLives(), width/2 - 120, height/2 + 150);
                    //show the maximum combo reached
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
    
    /**
     * Saves current game status into a text file
     * Each important variable to define the current status of the game is
     * stored in the file in a specific order
     */
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
            
            pw.println(Integer.toString(powerups.size()));
            
            for (int i = 0; i < powerups.size(); i++) {
                PowerUp powerup = powerups.get(i);
                pw.println(Integer.toString(powerup.getX()));
                pw.println(Integer.toString(powerup.getY()));
                
                switch(powerup.power) {
                    case speed:
                        pw.println(Integer.toString(0));
                        break;
                    case size:
                        pw.println(Integer.toString(1));
                        break;
                }
            }
            
            pw.println(Integer.toString(score));
            pw.close();
            System.out.println("SAVING...");

        } catch(IOException e) {
            System.out.println("BEEP BEEP");
            System.out.println(e.toString());
        }
    }
    
    /**
     * Load game from text file
     * This method open the designated text file and reads its contents
     * and assigns them to their designated variables
     */
    
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
            
            powerups.clear();
            
            int size = Integer.parseInt(br.readLine());
            
            for (int i = 0; i < size; i++) {
                int x = Integer.parseInt(br.readLine());
                int y = Integer.parseInt(br.readLine());
                int power = Integer.parseInt(br.readLine());
                powerups.add(new PowerUp(x, y, 60, 20, power));
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
    
    
    /**
     * to get starting
     * @return 
     */
    public boolean getStarting() {
        return starting;
    }
    
    /**
     * To get player
     * @return 
     */
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
