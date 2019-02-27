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

    public Ball(int x, int y, int width, int height, Game game) {
        super(x, y, width, height);
        this.game = game;
        
        maxVel = 10;
        
        xVel = 0;
        yVel = maxVel;
    }

    @Override
    public void tick() {
        setX(getX() + xVel);
        setY(getY() + yVel);
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.gray);
        g.fillOval(x, y, width, height);
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
}
