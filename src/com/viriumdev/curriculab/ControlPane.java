

package com.viriumdev.curriculab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Garrett
 */
public class ControlPane extends Application {
    
    @Override
    public void start(Stage stage) throws Exception 
    {
        
        CLUtilities.unpackResources();
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLControlPane.fxml"));
        
        Scene scene = new Scene(root);
        
       stage.initStyle(StageStyle.TRANSPARENT);
       stage.setScene(scene);
       stage.setResizable(true);
       stage.show();
       
       
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        launch(args);
    }
    
}
