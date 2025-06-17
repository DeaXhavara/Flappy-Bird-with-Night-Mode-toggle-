package application;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FlappyBirdGame {
    private final int W = 500;
    private final int H = 600;
    
    private Stage primaryStage;
    private Group root;
    private Scene scene;
    private Timeline gameLoop;
    
    private Bird bird;
    private Pipe pipe;
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    private BackgroundManager backgroundManager;
    
    private boolean gameOver;
    private int ticks;
    private int lastTransitionScore = 0;
    
    private Button restartButton;
    private ImageView gameOverImg;
    private ImageView startMessageImg;
    
    private Image pipeImage;
    private Image pipeNightImage;
    private ImagePattern pipePattern;
    private ImagePattern pipeNightPattern;
    
    public void start(Stage window) {
        primaryStage = window;
        primaryStage.setTitle("Flappy Bird");
        primaryStage.setHeight(H);
        primaryStage.setWidth(W);
        primaryStage.setResizable(false);
        
        root = new Group();
        
      
        soundManager = new SoundManager(getClass());
        
        
        Image backgroundImage = new Image(getClass().getResource("/resources/scene.png").toExternalForm());
        Image nightBackgroundImage = new Image(getClass().getResource("/resources/night.png").toExternalForm());
        Image groundImage = new Image(getClass().getResource("/resources/ground.png").toExternalForm());
        Image groundNightImage = new Image(getClass().getResource("/resources/ground-night.png").toExternalForm());
        Image cloudImage = new Image(getClass().getResource("/resources/cloud.png").toExternalForm());
        
        pipeImage = new Image(getClass().getResource("/resources/pipes.png").toExternalForm());
        pipeNightImage = new Image(getClass().getResource("/resources/pipes-night.png").toExternalForm());
        pipePattern = new ImagePattern(pipeImage);
        pipeNightPattern = new ImagePattern(pipeNightImage);
        
        backgroundManager = new BackgroundManager(root, W, H, backgroundImage, nightBackgroundImage, 
                                                groundImage, groundNightImage, cloudImage, pipeImage, pipeNightImage);
        
        Image[] numberImages = new Image[10];
        for (int i = 0; i < 10; i++) {
            numberImages[i] = new Image(getClass().getResource("/resources/" + i + ".png").toExternalForm());
        }
        scoreManager = new ScoreManager(root, numberImages);
        
        
        pipe = new Pipe(pipePattern, pipeNightPattern);
        
        
        Image[] birdFrames = new Image[] {
            new Image(getClass().getResource("/resources/yellowbird-downflap.png").toExternalForm()),
            new Image(getClass().getResource("/resources/yellowbird-midflap.png").toExternalForm()),
            new Image(getClass().getResource("/resources/yellowbird-upflap.png").toExternalForm())
        };
        bird = new Bird(birdFrames);
        root.getChildren().add(bird.getShape());
        
     
        gameOverImg = new ImageView(new Image(getClass().getResource("/resources/gameover.png").toExternalForm()));
        gameOverImg.setFitWidth(700);
        gameOverImg.setPreserveRatio(true);
        
        startMessageImg = new ImageView(new Image(getClass().getResource("/resources/message.png").toExternalForm()));
        startMessageImg.setFitWidth(280);
        startMessageImg.setPreserveRatio(true);
        
        restartButton = createRestartButton();
        
        gameLoop = createGameLoop();
        
        scene = new Scene(root);
        scene.setFill(Color.LIGHTPINK);
        
        restart();
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Button createRestartButton() {
        Button btn = new Button();
        
        Image tryAgainImage = new Image(getClass().getResource("/resources/tryagain.png").toExternalForm());
        ImageView tryAgainView = new ImageView(tryAgainImage);
        tryAgainView.setFitWidth(120);
        tryAgainView.setFitHeight(100);
        
        btn.setGraphic(tryAgainView);
        btn.setText("");
        btn.setStyle(
            "-fx-background-color: linear-gradient(#ffb6c1, #ff69b4);" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 20;" +
            "-fx-border-color: white;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 2;"
        );
        
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: linear-gradient(#ffc36a,#ff625b);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 20;" +
            "-fx-border-color: white;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 2;"
        ));
        
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: linear-gradient(#ffb6c1, #ff69b4);" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 10 20;" +
            "-fx-border-color: white;" +
            "-fx-border-radius: 20;" +
            "-fx-border-width: 2;"
        ));
        
        btn.setOnMouseClicked(k -> {
            root.getChildren().removeAll(scoreManager.getScoreDigits());
            soundManager.playSwooshingSound();
            restart();
        });
        
        return btn;
    }
    
    private Timeline createGameLoop() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        
        KeyFrame moveFrame = new KeyFrame(Duration.millis(20), e -> {
            ticks++;
            
            if (ticks % 2 == 0 && !gameOver) {
                bird.applyGravity(1);
            }
            
           
            backgroundManager.moveCloud(W);
            
            if (!gameOver) bird.update();
            
           
            scene.setOnKeyReleased(k -> {
                if (k.getCode() == KeyCode.SPACE) {
                    if (!gameOver) {
                        bird.jump();
                        soundManager.playWingSound();
                    }
                }
            });
            
            checkCollisions();
            
            if (gameOver && !root.getChildren().contains(restartButton)) {
                root.getChildren().add(restartButton);
            }
        });
        
        KeyFrame pipeFrame = new KeyFrame(Duration.millis(20), e -> {
            if (!gameOver) {
                pipe.movePipes();
                if (pipe.checkPassedPipe(bird, scoreManager)) {
                    if (scoreManager.getScore() % 2 == 0) {
                        soundManager.playPointSound();
                    }
                    int currentScore = scoreManager.getScore() / 2;
                    if (currentScore % 5 == 0 && currentScore != lastTransitionScore && 
                        currentScore > 0 && !backgroundManager.isTransitioning()) {
                        lastTransitionScore = currentScore;
                        if (!backgroundManager.isNightMode()) {
                            backgroundManager.transitionToNight(pipe, soundManager);
                        } else {
                            backgroundManager.transitionToDay(pipe, soundManager);
                        }
                    }
                }
            }
        });
        
        timeline.getKeyFrames().addAll(moveFrame, pipeFrame);
        return timeline;
    }
    
    private void checkCollisions() {
        if (pipe.collidesWithBird(bird)) {
            if (!gameOver) {
                soundManager.playHitSound();
                soundManager.playDieSound();
            }
            gameOver = true;
            bird.setYMotion(0);
            showGameOver();
            return;
        }
        
        if (bird.getShape().getCenterY() > H - 120 || bird.getShape().getCenterY() < 0) {
            if (!gameOver) {
                soundManager.playHitSound();
                soundManager.playDieSound();
            }
            gameOver = true;
            bird.setYMotion(0);
            showGameOver();
        }
    }
    
    private void showGameOver() {
        scoreManager.showGameOverScore(W, H);
        
        gameOverImg.setLayoutX((W - gameOverImg.getFitWidth()) / 2);
        gameOverImg.setLayoutY(((H - gameOverImg.getFitHeight()) / 2) - 450);
        if (!root.getChildren().contains(gameOverImg)) {
            root.getChildren().add(gameOverImg);
        }
        

        if (!root.getChildren().contains(restartButton)) {
            restartButton.setLayoutX((W - 200) / 2);
            restartButton.setLayoutY(H / 2 + 100);
            root.getChildren().add(restartButton);
        }
        
        gameLoop.pause();
        bird.stopAnimation();
    }
    
    public void restart() {
        bird.reset(W / 2 - 1, H / 2 + 125);
        
        gameOver = false;
        ticks = 0;
        lastTransitionScore = 0;
        
  
        backgroundManager.reset();
        

        pipe.setNightMode(false);
        
        
        scoreManager.reset();
        
     
        scoreManager.hideScoreDisplay();
        
       
        root.getChildren().remove(restartButton);
        root.getChildren().removeAll(pipe.getColumns());
        root.getChildren().remove(gameOverImg);
        
        pipe.clear();
        
        pipe.setDayModePattern(pipePattern);
        pipe.setNightModePattern(pipeNightPattern);
        

        for (int i = 0; i < 100; i++) {
            pipe.addColumn(W, H);
        }
        

        pipe.forceUpdateAllPipes(false);

        startMessageImg.setLayoutX((W - startMessageImg.getFitWidth()) / 2);
        startMessageImg.setLayoutY(((H - startMessageImg.getFitHeight()) / 2) - 250);
        gameLoop.pause();
        if (!root.getChildren().contains(startMessageImg)) {
            root.getChildren().add(startMessageImg);
        }
        

        scene.setOnKeyReleased(k -> {
            if (k.getCode() == KeyCode.SPACE) {
                soundManager.playSwooshingSound();
                root.getChildren().addAll(pipe.getColumns());
                root.getChildren().remove(startMessageImg);

                scoreManager.updateScoreDisplay(0);
                gameLoop.play();
            }
        });
    }
}