module DCIT308.GROUP.PROJECT {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires java.sql;
    requires org.docx4j.openxml_objects;
    requires jakarta.xml.bind;
    requires org.glassfish.jaxb.runtime;
    requires org.slf4j.simple;
    requires org.docx4j.core;

    opens Main;
    opens Main.Controllers;
    opens Main.Models;
    opens Main.Utils;
    opens Main.Services;
}