module com.example.hangmangame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.hangmangame to javafx.fxml;
    exports com.example.hangmangame;
}