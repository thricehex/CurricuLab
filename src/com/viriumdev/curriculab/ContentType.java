/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viriumdev.curriculab;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;




/**
 *
 * @author Garrett T. Smith
 */
public class ContentType {
    
    private final String parentValue;
    private final String childValue;
    private Label title;
    private WebView body;
    private WebEngine engine;
    private FlowPane pane;
    private Button remove;
    
    private FXMLMainController controller;
    private Hyperlink refLink;
    
    public ContentType(String parentValue, String childValue)
    {
        this.parentValue = parentValue;
        this.childValue = childValue;
    }
    
    public String getParentValue()
    {
        return parentValue;
    }
    
    public String getChildValue()
    {
        return childValue;
    }
    
    @Override
    public String toString()
    {
        return parentValue + " : " + childValue;
    }
    
    public Stage getViewingPane()
    {
        Stage viewingStage = getStandardViewLayout();
        title.setText(parentValue);
        engine.loadContent(childValue);
       return viewingStage;
    }
    
    protected Stage getStandardViewLayout()
    {
        pane = new FlowPane();
        pane.setPrefHeight(800);
        pane.setPrefWidth(500);
     
        title = new Label();
        title.setFont(Font.font("Verdana", 20));
        
        remove = new Button("Remove");
        remove.setOnAction(
                
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if(controller != null) {
                            controller.removeReference(ContentType.this, refLink);
                            }
                        else {
                            CLUtilities.notify("An error occurred. Reference not available to remove.");
                        }
                    }
                });
       
        
        body = new WebView();
        engine = body.getEngine();
        body.setManaged(true);
        body.setMinHeight(750);
        body.setMinWidth(450);
        
        
       pane.getChildren().add(title);
       pane.getChildren().add(remove);
       pane.setHgap(300);
       pane.setVgap(25);
       pane.getChildren().add(body);
        Scene scene = new Scene(pane);
        Stage viewingStage = new Stage(StageStyle.UTILITY);
        viewingStage.setScene(scene);
        viewingStage.setResizable(true);
        return viewingStage;
    }
   
    public FlowPane getViewPane()
    {
        return pane;
    }
    
    public Label getViewHeader()
    {
        return title;
    }
    
    public WebEngine getViewEngine()
    {
        return engine;
    }
    
    public void setParentController(FXMLMainController controller) {
        
        this.controller = controller;
    }
    
    public void setRefLink(Hyperlink refLink) {
        
        this.refLink = refLink;
    }
    
    
}
