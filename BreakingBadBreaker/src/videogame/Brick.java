/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videogame;

import java.awt.Graphics;

/**
 *
 * @author charles
 */
public class Brick extends Item {
    
    private Game game;
    private boolean broken;
    
    private int lives;
    
    private Animation expAni;
    
    private boolean recentBroken;
    
    /**
     * to create a new brick
     * @param x
     * @param y
     * @param width
     * @param height
     * @param game 
     */
    public Brick(int x, int y, int width, int height, Game game) {
        super(x, y, width, height);
        this.game = game;
        broken = false;
        
        expAni = new Animation(Assets.animationExplosion, 60);
        recentBroken = false;
        
        lives = 2;
    }
    
    /**
     * to get broken
     * @return 
     */
    public boolean isBroken(){
        return broken;
    }
    
    /**
     * to set broken
     * @param broken 
     */
    public void setBroken(boolean broken){
        this.broken = broken;
    }
    
    /**
     * To update the object in a frame
     */
    @Override
    public void tick() {
        
        //Check if it has been recently broken to animate the explosion
        if (recentBroken) {
            expAni.tick();
            
            //If animation is done, set recentBroken to false
            if (expAni.isDone()) {
                expAni.setDone(false);
                recentBroken = false;
            }
        }
    }
    
    /**
     * To render the object in the canvas
     * @param g 
     */
    @Override
    public void render(Graphics g) {
        if(!broken){
            if (lives == 1) {
                g.drawImage(Assets.brickHalf, getX(), getY(), getWidth(), getHeight(), null);
            } else {
                g.drawImage(Assets.brick, getX(), getY(), getWidth(), getHeight(), null);
            }
        } else if (recentBroken) {
            g.drawImage(expAni.getCurrentFrame(), getX()-15, getY()-35, null);
        }
    }
    
    /**
     * To set recentBroken
     * @param recentBroken 
     */
    public void setRecentBroken(boolean recentBroken) {
        this.recentBroken = recentBroken;
    }
    
    /**
     * to get recentBroken
     * @return 
     */
    public boolean isRecentBroken() {
        return recentBroken;
    }
    
    /**
     * To get lives
     * @return 
     */
    public int getLives() {
        return lives;
    }
    
    /**
     * To set lives
     * @param lives 
     */
    public void setLives(int lives) {
        this.lives = lives;
    }
}
