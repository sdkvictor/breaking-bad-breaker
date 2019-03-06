/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videogame;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author charles
 */
public class PowerUp extends Item {
    
    private int speed;
    private BufferedImage image;
    
    public static enum Power {speed, size};
    
    Power power;

    public PowerUp(int x, int y, int width, int height, int power) {
        super(x, y, width, height);
        speed = 5;
        
        switch(power) {
            case 0:
                this.power = Power.speed;
                image = Assets.fastPower;
                break;
            case 1:
                this.power = Power.size;
                image = Assets.bigPower;
                break;
        }
    }

    @Override
    public void tick() {
        setY(getY() + speed);
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
    }
    
}
