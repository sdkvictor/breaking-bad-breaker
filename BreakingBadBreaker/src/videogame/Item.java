package videogame;


import java.awt.Graphics;
import java.awt.Rectangle;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author charles
 */
public abstract class Item {
    
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    
    public Item(int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }
    
    public Rectangle getPerimeter() {
        return new Rectangle(x, y, width, height);
    }
    
    public boolean intersects(Item item) {
        return getPerimeter().intersects(item.getPerimeter());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    
        public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    
    public abstract void tick();
    public abstract void render(Graphics g);
}
