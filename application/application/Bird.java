package application;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;

public class Bird {
    private Ellipse birdShape;
    private int yMotion;
    private Image[] birdFrames;
    private int currentFrameIndex= 0;
    private Timeline birdAnimation;
    
    public Bird(Image[] frames) {
        this.birdFrames = frames;
        ImagePattern ip = new ImagePattern(birdFrames[1]);
        
        birdShape = new Ellipse();
        birdShape.setFill(ip);
        birdShape.setRadiusX((birdFrames[1].getWidth() / 2) + 2);
        birdShape.setRadiusY((birdFrames[1].getHeight() / 2) + 2);
        
        birdAnimation = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            currentFrameIndex = (currentFrameIndex + 1) % birdFrames.length;
            birdShape.setFill(new ImagePattern(birdFrames[currentFrameIndex]));
        }));
        birdAnimation.setCycleCount(Animation.INDEFINITE);
        birdAnimation.play();
        
        yMotion = 0;
    }
    
    public void jump() {
        if (yMotion > 0) yMotion = 0;
        yMotion = -9;
    }
    
    public void update() {
        birdShape.setCenterY(birdShape.getCenterY() + yMotion);
        birdShape.setRotate(yMotion * 2);
    }
    
    public void applyGravity(int gravity) {
        yMotion += gravity;
        if (yMotion > 12) yMotion = 12;
    }
    
    public void reset(double x, double y) {
        birdShape.setCenterX(x);
        birdShape.setCenterY(y);
        birdShape.setRotate(0);
        
        
        yMotion = 0;
        
       
        if (birdAnimation != null) {
            birdAnimation.stop(); 
            currentFrameIndex = 0;
            birdShape.setFill(new ImagePattern(birdFrames[currentFrameIndex]));
            birdAnimation.play(); 
        }
    }
    
    
    public void stopAnimation() {
        if (birdAnimation != null) {
            birdAnimation.pause();
        }
    }
    
    public Ellipse getShape() {
        return birdShape;
    }
    
    public int getYMotion() {
        return yMotion;
    }
    
    public void setYMotion(int yMotion) {
        this.yMotion = yMotion;
    }
}