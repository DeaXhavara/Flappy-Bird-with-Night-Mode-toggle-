package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Game extends Application {
    @Override
    public void start(Stage primaryStage) {
        FlappyBirdGame game = new FlappyBirdGame();
        game.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}