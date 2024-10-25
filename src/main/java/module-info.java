module org.example.proto {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.proto to javafx.fxml;
    exports org.example.proto;
}