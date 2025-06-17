package application;

import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ScoreManager {
    private int score;
    private Image[] numberImages;
    private ArrayList<ImageView> scoreDigits;
    private Group root;
    
    public ScoreManager(Group root, Image[] numberImages) {
        this.root = root;
        this.numberImages = numberImages;
        scoreDigits = new ArrayList<>();
        score = 0;
    }
    
    public void increaseScore() {
        score++;
        if (score % 2 == 0) {
            updateScoreDisplay(score / 2);
        }
    }
    
    public void updateScoreDisplay(int displayScore) {
        root.getChildren().removeAll(scoreDigits);
        scoreDigits.clear();

        String scoreStr = Integer.toString(displayScore);
        int xOffset = 20;

        for (int i = 0; i < scoreStr.length(); i++) {
            int digit = Character.getNumericValue(scoreStr.charAt(i));
            ImageView digitView = new ImageView(numberImages[digit]);

         
            if (digit == 1) {
                digitView.setFitWidth(24);
                digitView.setFitHeight(44);
                digitView.setX(xOffset + i * 35 + 10);
            } else {
                digitView.setFitWidth(35);
                digitView.setFitHeight(44);
                digitView.setX(xOffset + i * 35);
            }

            digitView.setY(20);
            scoreDigits.add(digitView);
            root.getChildren().add(digitView);
        }
    }
    
    public void showGameOverScore(int screenWidth, int screenHeight) {
        String scoreStr = Integer.toString(score / 2);
        int xOffset = (screenWidth - 70 * scoreStr.length()) / 2;
        int yOffset = screenHeight / 2 - 10;

        root.getChildren().removeAll(scoreDigits);
        scoreDigits.clear();

        for (int i = 0; i < scoreStr.length(); i++) {
            int digit = Character.getNumericValue(scoreStr.charAt(i));
            ImageView digitView = new ImageView(numberImages[digit]);

            if (digit == 1) {
                digitView.setFitWidth(24);
                digitView.setFitHeight(44);
                digitView.setX(xOffset + i * 40 + 10);
            } else {
                digitView.setFitWidth(35);
                digitView.setFitHeight(44);
                digitView.setX(xOffset + i * 40);
            }

            digitView.setY(yOffset);
            scoreDigits.add(digitView);
            root.getChildren().add(digitView);
        }
    }
    
    public void reset() {
        root.getChildren().removeAll(scoreDigits);
        scoreDigits.clear();
        score = 0;
        updateScoreDisplay(0);
    }
    
    public ArrayList<ImageView> getScoreDigits() {
        return scoreDigits;
    }
    
    public int getScore() {
        return score;
    }
    public void hideScoreDisplay() {
    
        if (root != null && scoreDigits != null) {
            root.getChildren().removeAll(scoreDigits);
        }
    }
}