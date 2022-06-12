module com.example.mylittleplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.media;
    requires javafx.base;
    requires org.apache.commons.io;
    requires mp3agic;
    exports com.example.mylittleplayer;
    opens com.example.mylittleplayer;
}