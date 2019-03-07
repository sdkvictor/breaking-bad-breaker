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

    public static BufferedImage background; //background image
    public static BufferedImage player; //player paddle image
    public static BufferedImage brick; //brick sprite
    public static BufferedImage brickHalf; //brick sprite after a first hit
    public static BufferedImage life; //lives sprite
    public static BufferedImage fastPower; //powerup to increase player's speed
    public static BufferedImage bigPower; //powerup to increase player's size
    
    public static BufferedImage ballSprites; //contains all the sprites of the ball's animation
    public static BufferedImage explosionSprites; //contains all the sprites of the explosion after destroying a brick
    
    public static BufferedImage animationBall[]; //array of ball's animation sprites
    public static BufferedImage animationExplosion[]; //array of explosion's animation sprites
    
    public static BufferedImage startScreen;
    public static BufferedImage instructions;
    public static SoundClip explosionSound;
    public static SoundClip backMusic;
    
    /**
     * Initialize all assets
     */
    public static void init() {
        background = ImageLoader.loadImage("/images/back_break.jpg");
        life = ImageLoader.loadImage("/images/heart.png");
        brick = ImageLoader.loadImage("/images/brick.png");
        brickHalf = ImageLoader.loadImage("/images/brick_half.png");
        ballSprites = ImageLoader.loadImage("/images/ball_sprite.png");
        explosionSprites = ImageLoader.loadImage("/images/explosion_sprite.png");
        fastPower = ImageLoader.loadImage("/images/fastpower.png");
        bigPower = ImageLoader.loadImage("/images/bigpower.png");
        
        explosionSound = new SoundClip("/sounds/explosion.wav");
        backMusic = new SoundClip("/sounds/theme.wav");
        
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
        
        startScreen = ImageLoader.loadImage("/images/startscreen.png");
        instructions = ImageLoader.loadImage("/images/instrucciones.png");
        
        int count = 0;
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                animationExplosion[count] = sheetExp.crop(j*128, i*128, 128, 128);
                count++;
                if (j == 1 && i == 3)
                    break;
            }
        }
        
        player = ImageLoader.loadImage("/images/needle.png");
        
    }
}
