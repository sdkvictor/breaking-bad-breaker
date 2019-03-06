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
public class Brick extends Item {
    
    private Game game;
    private boolean broken;
    
    private Animation expAni;
    
    private boolean recentBroken;
    
    public Brick(int x, int y, int width, int height, Game game) {
        super(x, y, width, height);
        this.game = game;
        broken = false;
        
        expAni = new Animation(Assets.animationExplosion, 60);
        recentBroken = false;
    }
    
    public boolean isBroken(){
        return broken;
    }
    
    public void setBroken(boolean broken){
        this.broken = broken;
    }
    
    @Override
    public void tick() {
        if (recentBroken) {
            expAni.tick();
            if (expAni.isDone()) {
                expAni.setDone(false);
                recentBroken = false;
            }
        }
    }
    
    @Override
    public void render(Graphics g) {
        if(!broken){
            g.drawImage(Assets.brick, getX(), getY(), getWidth(), getHeight(), null);
        } else if (recentBroken) {
            g.drawImage(expAni.getCurrentFrame(), getX()-15, getY()-35, null);
        }
    }

    public void setRecentBroken(boolean recentBroken) {
        this.recentBroken = recentBroken;
    }

    public boolean isRecentBroken() {
        return recentBroken;
    }
}
