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
    requires mp3agic;
    requires open.m3u8;
    exports com.example.mylittleplayer;
    opens com.example.mylittleplayer;
}