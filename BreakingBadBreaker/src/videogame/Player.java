/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videogame;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author charles
 */
public class Player extends Item {
    
    private Game game;
    private int speed;
    
    private final int origSpeed;
    private final int origSize;
    
    private int lives;
    
    private int speedCounter;
    private int sizeCounter;
    private boolean isSpeedPower;
    private boolean isSizePower;
    
    /**
     * To create a new player object
     * @param x
     * @param y
     * @param width
     * @param height
     * @param game 
     */
    public Player(int x, int y, int width, int height, Game game) {
        super(x, y, width, height);
        this.game = game;
        
        origSpeed = 10;
        speed = origSpeed;
        lives = 3;
        
        speedCounter = 0;
        sizeCounter = 0;
        isSpeedPower = false;
        isSizePower = false;
        
        origSize = width;
    }
    
    /**
     * updates the object every frame
     */
    @Override
    public void tick() {
        
        if (game.getKeyManager().left) {
            setX(getX() - speed);
        }
        
        if (game.getKeyManager().right) {
            setX(getX() + speed);
        }
        
        if (getX() + getWidth() > game.getWidth()) {
            setX(game.getWidth() - getWidth());
        }
        
        if (getX() < 0) {
            setX(0);
        }
        
        //Check if fast speed powerup is active
        if (isSpeedPower) {
            
            //Increase counter every frame
            speedCounter++;
            
            //If counter reaches 200, at 50 fps it means that 4 seconds have passed
            //which will be the limit of time the powerup if active
            //so deactivate the powerup
            if (speedCounter == 200) {
                speed = origSpeed;
                speedCounter = 0;
                isSpeedPower = false;
            }
        }
        
        
        //Same logic as speedPower is applied to isSizePower
        if (isSizePower) {
            sizeCounter++;
            
            if (sizeCounter == 200) {
                width = origSize;
                sizeCounter = 0;
                isSizePower = false;
            }
        }
    }
    
    /**
     * Paints the object to the canvas
     * @param g 
     */
    @Override
    public void render(Graphics g) {
        g.setColor(Color.white);
        g.drawImage(Assets.player, getX(), getY(), getWidth(), getHeight(), null);
        
        if (isSpeedPower) {
            g.drawImage(Assets.fastPower, 900, 40, 100, 35, null);
        }
        
        if (isSizePower) {
            g.drawImage(Assets.bigPower, 1050, 40, 100, 35, null);
        }
    }
    
    /**
     * Activate the special powerup for faster speed
     */
    public void activateFastSpeed() {
        isSpeedPower = true;
        speedCounter = 0;
        speed = origSpeed *3;
    }
    
    /**
     * Activate the special powerup for bigger pad size
     */
    public void activateBigSize() {
        isSizePower = true;
        sizeCounter = 0;
        width = origSize *2;
    }
    
    /**
     * To get the player lives
     * @return 
     */
    public int getLives() {
        return lives;
    }
    
    /**
     * To set the player lives
     * @param lives 
     */
    public void setLives(int lives) {
        this.lives = lives;
    }
    
    /**
     * to get the current speed
     * @return 
     */
    public int getSpeed() {
        return speed;
    }
    
    /**
     * To set the current speed
     * @param speed 
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    /**
     * To check if isSpeedPower is active
     * @return 
     */
    public boolean isIsSpeedPower() {
        return isSpeedPower;
    }
    
    /**
     * To check if isSizePower is active
     * @return 
     */
    public boolean isIsSizePower() {
        return isSizePower;
    }
    
    /**
     * To set isSizePower
     * @param isSizePower 
     */
    public void setIsSizePower(boolean isSizePower) {
        this.isSizePower = isSizePower;
    }
    
    /**
     * To set isSpeedPower
     * @param isSpeedPower 
     */
    public void setIsSpeedPower(boolean isSpeedPower) {
        this.isSpeedPower = isSpeedPower;
    }
    
    /**
     * To get speedCounter
     * @return 
     */
    public int getSpeedCounter() {
        return speedCounter;
    }
    
    /**
     * to get sizeCounter
     * @return 
     */
    public int getSizeCounter() {
        return sizeCounter;
    }
    
    /**
     * to set speedCounter
     * @param speedCounter 
     */
    public void setSpeedCounter(int speedCounter) {
        this.speedCounter = speedCounter;
    }
    
    /**
     * to set sizeCounter
     * @param sizeCounter 
     */
    public void setSizeCounter(int sizeCounter) {
        this.sizeCounter = sizeCounter;
    }
}
