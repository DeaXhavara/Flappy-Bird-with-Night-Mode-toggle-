package application;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.SequentialTransition;
import javafx.animation.PauseTransition;

public class BackgroundManager {
    private ImageView dayBackgroundView;
    private ImageView nightBackgroundView;
    private ImageView dayGroundView;
    private ImageView nightGroundView;
    private ImageView cloudView;
    
    private Image groundImage;
    private Image groundNightImage;
    private Image pipeImage;
    private Image pipeNightImage;
    private ImagePattern pipePattern;
    private ImagePattern pipeNightPattern;
    
    private boolean isNightMode = false;
    private boolean isTransitioning = false;
    
    private int cloudX, cloudY;
    
    public BackgroundManager(Group root, int screenWidth, int screenHeight, Image backgroundImage, Image nightBackgroundImage, 
                            Image groundImage, Image groundNightImage, Image cloudImage, Image pipeImage, Image pipeNightImage) {
        this.groundImage = groundImage != null ? groundImage : createFallbackImage(Color.GREEN);
        this.groundNightImage = groundNightImage != null ? groundNightImage : createFallbackImage(Color.DARKGREEN);
        this.pipeImage = pipeImage != null ? pipeImage : createFallbackImage(Color.GREEN);
        this.pipeNightImage = pipeNightImage != null ? pipeNightImage : createFallbackImage(Color.DARKGREEN);
        
        this.pipePattern = new ImagePattern(this.pipeImage);
        this.pipeNightPattern = new ImagePattern(this.pipeNightImage);
        
        dayBackgroundView = new ImageView(backgroundImage != null ? backgroundImage : createFallbackImage(Color.SKYBLUE));
        dayBackgroundView.setFitWidth(screenWidth);
        dayBackgroundView.setFitHeight(screenHeight);
        dayBackgroundView.setPreserveRatio(false);
        root.getChildren().add(dayBackgroundView);
        
        nightBackgroundView = new ImageView(nightBackgroundImage != null ? nightBackgroundImage : createFallbackImage(Color.DARKBLUE));
        nightBackgroundView.setFitWidth(screenWidth);
        nightBackgroundView.setFitHeight(screenHeight);
        nightBackgroundView.setPreserveRatio(false);
        nightBackgroundView.setOpacity(0); 
        root.getChildren().add(nightBackgroundView);
        
        dayGroundView = new ImageView(this.groundImage);
        dayGroundView.setFitWidth(screenWidth);
        dayGroundView.setFitHeight(120);
        dayGroundView.setLayoutY(screenHeight - 120);
        dayGroundView.setPreserveRatio(false);
        root.getChildren().add(dayGroundView);
        
        nightGroundView = new ImageView(this.groundNightImage);
        nightGroundView.setFitWidth(screenWidth);
        nightGroundView.setFitHeight(120);
        nightGroundView.setLayoutY(screenHeight - 120);
        nightGroundView.setPreserveRatio(false);
        nightGroundView.setOpacity(0); 
        root.getChildren().add(nightGroundView);
        
        Image safeCloudImage = cloudImage != null ? cloudImage : createFallbackImage(Color.WHITE);
        cloudView = new ImageView(safeCloudImage);
        cloudX = screenWidth + (int) safeCloudImage.getWidth();
        cloudView.setX(cloudX);
        cloudY = 10 + (int) (Math.random() * 100);
        cloudView.setY(cloudY);
        root.getChildren().add(cloudView);
    }
    
    private Image createFallbackImage(Color color) {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(10, 10);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillRect(0, 0, 10, 10);
        return canvas.snapshot(null, null);
    }
    
    public void moveCloud(int screenWidth) {
        cloudX -= 2;
        cloudView.setX(cloudX);
        if (cloudX < -(int) cloudView.getImage().getWidth()) {
            cloudX = screenWidth + (int) cloudView.getImage().getWidth();
            cloudView.setX(cloudX);
            cloudY = 10 + (int) (Math.random() * 100);
            cloudView.setY(cloudY);
        }
    }
    
    public void transitionToNight(Pipe pipe, SoundManager soundManager) {
        if (isTransitioning) return;
        isTransitioning = true;
        
        soundManager.playSwooshingSound();

        Duration duration = Duration.seconds(1.8);
        ParallelTransition masterTransition = new ParallelTransition();

        FadeTransition fadeBackground = new FadeTransition(duration, nightBackgroundView);
        fadeBackground.setFromValue(nightBackgroundView.getOpacity());
        fadeBackground.setToValue(1.0);
        masterTransition.getChildren().add(fadeBackground);

        FadeTransition fadeNightGround = new FadeTransition(duration, nightGroundView);
        fadeNightGround.setFromValue(nightGroundView.getOpacity());
        fadeNightGround.setToValue(1.0);
        masterTransition.getChildren().add(fadeNightGround);

        FadeTransition fadeDayGround = new FadeTransition(duration, dayGroundView);
        fadeDayGround.setFromValue(dayGroundView.getOpacity());
        fadeDayGround.setToValue(0.0);
        masterTransition.getChildren().add(fadeDayGround);

       
        for (Rectangle pipeCol : pipe.getColumns()) {
           
            SequentialTransition pipeTransition = new SequentialTransition();
            
          
            FadeTransition fadeHalfOut = new FadeTransition(Duration.seconds(1.0), pipeCol);
            fadeHalfOut.setFromValue(2.0);
            fadeHalfOut.setToValue(0.5);
            
           
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.0), pipeCol);
            fadeIn.setFromValue(0.2);
            fadeIn.setToValue(2.0);
            
     
            PauseTransition patternPause = new PauseTransition(Duration.millis(50));
            patternPause.setOnFinished(event -> pipeCol.setFill(pipeNightPattern));
            
         
            pipeTransition.getChildren().addAll(fadeHalfOut, patternPause, fadeIn);
            masterTransition.getChildren().add(pipeTransition);
        }

        masterTransition.setOnFinished(e -> {
            isNightMode = true;
            isTransitioning = false;
            pipe.setNightMode(true);
        });

        masterTransition.play();
    }

    public void transitionToDay(Pipe pipe, SoundManager soundManager) {
        if (isTransitioning) return;
        isTransitioning = true;
        
        soundManager.playSwooshingSound();

        Duration duration = Duration.seconds(1.8);
        ParallelTransition masterTransition = new ParallelTransition();

      
        FadeTransition fadeBackground = new FadeTransition(duration, nightBackgroundView);
        fadeBackground.setFromValue(nightBackgroundView.getOpacity());
        fadeBackground.setToValue(0.0);
        masterTransition.getChildren().add(fadeBackground);

       
        FadeTransition fadeNightGround = new FadeTransition(duration, nightGroundView);
        fadeNightGround.setFromValue(nightGroundView.getOpacity());
        fadeNightGround.setToValue(0.0);
        masterTransition.getChildren().add(fadeNightGround);

        FadeTransition fadeDayGround = new FadeTransition(duration, dayGroundView);
        fadeDayGround.setFromValue(dayGroundView.getOpacity());
        fadeDayGround.setToValue(1.0);
        masterTransition.getChildren().add(fadeDayGround);

       
        for (Rectangle pipeCol : pipe.getColumns()) {
            FadeTransition pipeFadeOut = new FadeTransition(Duration.seconds(0.5), pipeCol);
            pipeFadeOut.setFromValue(1.0);
            pipeFadeOut.setToValue(0.7);
            
            FadeTransition pipeFadeIn = new FadeTransition(Duration.seconds(0.5), pipeCol);
            pipeFadeIn.setFromValue(0.7);
            pipeFadeIn.setToValue(1.0);
            
            
            pipeFadeOut.setOnFinished(event -> {
                pipeCol.setFill(pipePattern);
                pipeFadeIn.play();
            });
            
            masterTransition.getChildren().add(pipeFadeOut);
        }

        masterTransition.setOnFinished(e -> {
            isNightMode = false;
            isTransitioning = false;
            pipe.setNightMode(false);
        });

        masterTransition.play();
    }
    public void reset() {
        isTransitioning = false;
        
        isNightMode = false;
       
        nightBackgroundView.setOpacity(0);
        nightGroundView.setOpacity(0);
        dayGroundView.setOpacity(1.0);
        javafx.animation.Animation.Status.RUNNING.toString(); 
    }
    
    public void updatePipeAppearance(Pipe pipe) {
        if (pipe != null && pipe.getColumns() != null) {
            ImagePattern pattern = isNightMode ? pipeNightPattern : pipePattern;
            for (Rectangle pipeCol : pipe.getColumns()) {
                pipeCol.setFill(pattern);
                pipeCol.setOpacity(1.0);
            }
        }
    }
    
    public void forceDayMode(Pipe pipe) {
        isNightMode = false;
        isTransitioning = false;
        nightBackgroundView.setOpacity(0);
        nightGroundView.setOpacity(0);
        dayGroundView.setOpacity(1.0);
        
        if (pipe != null) {
            pipe.setNightMode(false);
            updatePipeAppearance(pipe);
        }
    }
    

    public void forceNightMode(Pipe pipe) {
        isNightMode = true;
        isTransitioning = false;
        nightBackgroundView.setOpacity(1.0);
        nightGroundView.setOpacity(1.0);
        dayGroundView.setOpacity(0.0);
        
        if (pipe != null) {
            pipe.setNightMode(true);
            updatePipeAppearance(pipe);
        }
    }
    
    public ImagePattern getPipePattern() {
        return isNightMode ? pipeNightPattern : pipePattern;
    }
    
    public boolean isNightMode() {
        return isNightMode;
    }
    
    public boolean isTransitioning() {
        return isTransitioning;
    }
}