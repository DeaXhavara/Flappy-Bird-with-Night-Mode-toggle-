package application;

import java.util.ArrayList;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;

public class Pipe {
    private ArrayList<Rectangle> columns;
    private ImagePattern pipePattern;
    private ImagePattern pipeNightPattern;
    private boolean isNightMode = false;
    
    public Pipe(ImagePattern dayPattern, ImagePattern nightPattern) {
        columns = new ArrayList<>();
        this.pipePattern = dayPattern;
        this.pipeNightPattern = nightPattern;
    }
    
    public void addColumn(int screenWidth, int screenHeight) {
        int space = 180;
        int width = 90;
        int minPipeHeight = 100;
        int maxPipeHeight = screenHeight - 120 - space - minPipeHeight;

        int height = minPipeHeight + (int) (Math.random() * (maxPipeHeight - minPipeHeight));
        int xPosition = screenWidth + width + (columns.size() * 160);
        int bottomY = screenHeight - 120 - height;
        
        Rectangle bottomColumn = new Rectangle(xPosition, bottomY, width, height);
        bottomColumn.setFill(isNightMode ? pipeNightPattern : pipePattern);

        int topHeight = screenHeight - 120 - height - space;
        Rectangle topColumn = new Rectangle(xPosition, 0, width, topHeight);
        topColumn.setFill(isNightMode ? pipeNightPattern : pipePattern);
        topColumn.setScaleY(-1);

        columns.add(topColumn);
        columns.add(bottomColumn);
    }
    
    public void movePipes() {
        for (int i = 0; i < columns.size(); i++) {
            Rectangle column = columns.get(i);
            column.setX(column.getX() - 5);
            
            if (column.getX() + column.getWidth() < 0) {
                columns.remove(i);
                i--;
            }
        }
    }
    
    public boolean collidesWithBird(Bird bird) {
        Ellipse birdShape = bird.getShape();
        double birdX = birdShape.getCenterX();
        double birdY = birdShape.getCenterY();
        double birdWidth = birdShape.getRadiusX() * 2;
        double birdHeight = birdShape.getRadiusY() * 2;

        Rectangle birdBox = new Rectangle(birdX - birdShape.getRadiusX() + 5, birdY - birdShape.getRadiusY() + 5, 
                                         birdWidth - 10, birdHeight - 10);
                                         
        for (Rectangle column : columns) {
            if (column.getBoundsInParent().intersects(birdBox.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean checkPassedPipe(Bird bird, ScoreManager scoreManager) {
        Ellipse birdShape = bird.getShape();
        for (int i = 0; i < columns.size(); i++) {
            Rectangle column = columns.get(i);
            if (column.getY() == 0 &&
                birdShape.getCenterX() + birdShape.getRadiusX() > column.getX() + column.getWidth() / 2 - 5 &&
                birdShape.getCenterX() + birdShape.getRadiusX() < column.getX() + column.getWidth() / 2 + 5) {
                scoreManager.increaseScore();
                return true;
            }
        }
        return false;
    }
    
    public void setNightMode(boolean nightMode) {
        this.isNightMode = nightMode;
    }
    
    public boolean isNightMode() {
        return isNightMode;
    }
    
    public void updatePipesPattern(ImagePattern pattern) {
        for (Rectangle pipe : columns) {
            pipe.setFill(pattern);
        }
    }
    
    public ArrayList<Rectangle> getColumns() {
        return columns;
    }
    
    public void clear() {
        columns.clear();
    }
    public void setDayModePattern(ImagePattern pattern) {
        this.pipePattern = pattern;
    }

    public void setNightModePattern(ImagePattern pattern) {
        this.pipeNightPattern = pattern;
    }

    public void forceUpdateAllPipes(boolean night) {
        ImagePattern pattern = night ? pipeNightPattern : pipePattern;
        for (Rectangle column : columns) {
            column.setFill(pattern);
            column.setOpacity(1.0);
        }
        isNightMode = night;
    }
}