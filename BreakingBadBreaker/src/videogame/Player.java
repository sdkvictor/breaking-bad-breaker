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
        
        if (isSpeedPower) {
            speedCounter++;
            
            if (speedCounter == 200) {
                speed = origSpeed;
                speedCounter = 0;
                isSpeedPower = false;
            }
        }
        
        if (isSizePower) {
            sizeCounter++;
            
            if (sizeCounter == 200) {
                width = origSize;
                sizeCounter = 0;
                isSizePower = false;
            }
        }
    }

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
    
    public void activateFastSpeed() {
        isSpeedPower = true;
        speedCounter = 0;
        speed = origSpeed *3;
    }
    
    public void activateBigSize() {
        isSizePower = true;
        sizeCounter = 0;
        width = origSize *2;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isIsSpeedPower() {
        return isSpeedPower;
    }

    public boolean isIsSizePower() {
        return isSizePower;
    }

    public void setIsSizePower(boolean isSizePower) {
        this.isSizePower = isSizePower;
    }

    public void setIsSpeedPower(boolean isSpeedPower) {
        this.isSpeedPower = isSpeedPower;
    }

    public int getSpeedCounter() {
        return speedCounter;
    }

    public int getSizeCounter() {
        return sizeCounter;
    }

    public void setSpeedCounter(int speedCounter) {
        this.speedCounter = speedCounter;
    }

    public void setSizeCounter(int sizeCounter) {
        this.sizeCounter = sizeCounter;
    }
}
