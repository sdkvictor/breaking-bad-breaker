/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videogame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author charles
 */
public class Ball extends Item {
    
    private Game game;
    private int xVel;
    private int yVel;
    private int lastPos;
    
    private int maxVel;
    
    private Animation ballAni;
    
    /**
     * To create a new ball object
     * @param x
     * @param y
     * @param width
     * @param height
     * @param game 
     */
    public Ball(int x, int y, int width, int height, Game game) {
        super(x, y, width, height);
        this.game = game;
        
        maxVel = 20;
        
        xVel = 0;
        yVel = 5;
        
        lastPos = 0;
        
        ballAni = new Animation(Assets.animationBall, 100);
    }
    
    /**
     * To update the object in a frame
     */
    @Override
    public void tick() {
        setLastPos(getY()+getHeight());
        
        //If game is just starting, attach ball to the position of the player
        if (game.getStarting()) {
            setX(game.getPlayer().getX() + game.getPlayer().getWidth() / 2 - getWidth() / 2);
            setY(game.getPlayer().getY() -  getHeight());
        }
        
        setX(getX() + xVel);
        setY(getY() + yVel);
        
        //Check collision with ceil
        if (getY() <= 0) {
            setyVel(getyVel() * -1);
        }
        
        //Check collision with left wall
        if (getX() <= 0) {
            setxVel(Math.abs(getxVel()));
        }
        
        //Check collision with right wall
        if (getX() + getWidth() >= game.getWidth()) {
            setxVel(Math.abs(getxVel()) * -1);
        }
        
        //Limit velocity with maxvel
        if (getxVel() > getMaxVel()) {
            setxVel(getMaxVel());
        }
        
        if (getyVel() > getMaxVel()) {
            setyVel(getMaxVel());
        }
        
        if (getY() <= 0) {
            setY(0);
        }
        
        if (getY() + getHeight() >= game.getWidth()) {
            setY(game.getWidth() - getWidth());
        }
        
        ballAni.tick();
    }
    
    /**
     * To render the object in the canvas
     * @param g 
     */
    @Override
    public void render(Graphics g) {
        g.drawImage(ballAni.getCurrentFrame(), getX(), getY(), getWidth(), getHeight(), null);
    }
    
    /**
     * To get xVel
     * @return 
     */
    public int getxVel() {
        return xVel;
    }
    
    /**
     * To get yVel
     * @return 
     */
    public int getyVel() {
        return yVel;
    }
    /**
     * To set xVel
     * @param xVel 
     */
    public void setxVel(int xVel) {
        this.xVel = xVel;
    }
    
    /**
     * To set yVel
     * @param yVel 
     */
    public void setyVel(int yVel) {
        this.yVel = yVel;
    }
    
    /**
     * to getMaxVel;
     * @return 
     */
    public int getMaxVel() {
        return maxVel;
    }
    
    /**
     * to get lastPos;
     * @return 
     */
    public int getLastPos(){
        return lastPos;
    }
    
    /**
     * To set lastPos;
     * @param lastPos 
     */
    public void setLastPos(int lastPos){
        this.lastPos = lastPos;
    }
    
    
}
