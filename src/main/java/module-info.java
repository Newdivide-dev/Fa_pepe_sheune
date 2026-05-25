module org.example.music {
    // Основные модули
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires jbcrypt;
    requires org.junit.jupiter.api;

    opens org.example.music to
            javafx.fxml,
            javafx.graphics,
            javafx.base,
            junit.jupiter.api,
            junit.jupiter.engine;

    exports org.example.music;
}