/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videogame;

import java.awt.image.BufferedImage;

/**
 *
 * @author charles
 */
public class Assets {

    public static BufferedImage background;
    public static BufferedImage player;
    public static BufferedImage brick;
    public static BufferedImage life;
    public static BufferedImage fastPower;
    
    public static BufferedImage ballSprites;
    public static BufferedImage explosionSprites;
    
    public static BufferedImage animationBall[];
    public static BufferedImage animationExplosion[];
    
    public static void init() {
        background = ImageLoader.loadImage("/images/back_break.jpg");
        life = ImageLoader.loadImage("/images/heart.png");
        brick = ImageLoader.loadImage("/images/nuclear_barrel_new.jpg");
        ballSprites = ImageLoader.loadImage("/images/ball_sprite.png");
        explosionSprites = ImageLoader.loadImage("/images/explosion_sprite.png");
        fastPower = ImageLoader.loadImage("/images/fastpower.png");
        
        SpriteSheet sheetBall = new SpriteSheet(ballSprites);
        SpriteSheet sheetExp = new SpriteSheet(explosionSprites);
        
        animationBall = new BufferedImage[16];
        animationExplosion = new BufferedImage[14];
        
        animationBall[0] = sheetBall.crop(15, 9, 70, 64);
        animationBall[1] = sheetBall.crop(123, 13, 60, 60);
        animationBall[2] = sheetBall.crop(226, 14, 58, 58);
        animationBall[3] = sheetBall.crop(20, 121, 56, 56);
        animationBall[4] = sheetBall.crop(127, 121, 54, 54);
        animationBall[5] = sheetBall.crop(235, 126, 52, 52);
        animationBall[6] = sheetBall.crop(29, 231, 50, 50);
        animationBall[7] = sheetBall.crop(135, 232, 48, 48);
        animationBall[8] = sheetBall.crop(243, 235, 46, 46);
        animationBall[15] = sheetBall.crop(123, 13, 60, 60);
        animationBall[14] = sheetBall.crop(226, 14, 58, 58);
        animationBall[13] = sheetBall.crop(20, 121, 56, 56);
        animationBall[12] = sheetBall.crop(127, 121, 54, 54);
        animationBall[11] = sheetBall.crop(235, 126, 52, 52);
        animationBall[10] = sheetBall.crop(29, 231, 50, 50);
        animationBall[9] = sheetBall.crop(135, 232, 48, 48);
        
        int count = 0;
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                animationExplosion[count] = sheetExp.crop(j*128, i*128, 128, 128);
                count++;
                if (j == 1 && i == 3)
                    break;
            }
        }
        
        //player = ImageLoader.loadImage("/images/mario.png");
        
    }
}
