package com.viriumdev.curriculab.api;

import com.viriumdev.curriculab.ContentType;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.stage.Stage;


/**
 * Wrapper class for an API response element
 * 
 * @author Garrett T. Smith
 */

public class APIElement extends ContentType {
    
    private String uri;
    
    public APIElement(String parent, String child)
    {
        super(parent, child);
    }
    
    public APIElement(String parent, String child, String uri)
    {
        super(parent, child);
        uri = uri.replaceAll("^\"|\"$", "");
        uri = uri.concat("&content-fmt=text");
        this.uri = uri;
    }
    
    @Override
    public Stage getViewingPane()
    {
        Stage stage = getStandardViewLayout();
        if(uri != null) 
        {
        
       APIConnection apiConn = new APIConnection(APIConnection.ARTICLE);
       APIResponse response = apiConn.rawQuery(uri);
       APIElement element = response.getAllParsedElements().get(0);
       getViewEngine().loadContent(element.getChildValue());
       getViewHeader().setText(element.getParentValue());
       
      
        } else
        {
            getViewHeader().setText(getParentValue());
            getViewEngine().loadContent(getChildValue());

        }
        
        return stage;
    }

}
