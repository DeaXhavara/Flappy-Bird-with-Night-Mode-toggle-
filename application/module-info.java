module FlappyBird {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens application to javafx.graphics, javafx.fxml, javafx.media;
}
