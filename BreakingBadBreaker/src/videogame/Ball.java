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
public class Ball extends Item {
    
    private Game game;
    private int xVel;
    private int yVel;
    
    private int maxVel;
    
    private Animation ballAni;

    public Ball(int x, int y, int width, int height, Game game) {
        super(x, y, width, height);
        this.game = game;
        
        maxVel = 20;
        
        xVel = 0;
        yVel = 5;
        
        ballAni = new Animation(Assets.animationBall, 100);
    }

    @Override
    public void tick() {
        
        if (game.getStarting()) {
            setX(game.getPlayer().getX() + game.getPlayer().getWidth() / 2 - getWidth() / 2);
            setY(game.getPlayer().getY() -  getHeight());
        }
        
        setX(getX() + xVel);
        setY(getY() + yVel);
        
        if (getY() <= 0) {
            setyVel(getyVel() * -1);
        }
        
        if (getX() <= 0) {
            setxVel(Math.abs(getxVel()));
        }
        
        if (getX() + getWidth() >= game.getWidth()) {
            setxVel(Math.abs(getxVel()) * -1);
        }
        
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

    @Override
    public void render(Graphics g) {
        g.drawImage(ballAni.getCurrentFrame(), getX(), getY(), getWidth(), getHeight(), null);
        //g.setColor(Color.gray);
        //g.fillOval(x, y, width, height);
    }

    public int getxVel() {
        return xVel;
    }

    public int getyVel() {
        return yVel;
    }

    public void setxVel(int xVel) {
        this.xVel = xVel;
    }

    public void setyVel(int yVel) {
        this.yVel = yVel;
    }

    public int getMaxVel() {
        return maxVel;
    }
    
}
