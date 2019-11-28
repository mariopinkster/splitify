package nl.dimario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SplitPart {

    private String relativeNodePath;
    private SplitPart parent;
    private ObjectNode payLoad;
    List<SplitPart> children;

    public SplitPart( SplitPart parent, String relativeNodePath, ObjectNode payLoad) {
        this.parent = parent;
        this.relativeNodePath =  relativeNodePath;
        this.payLoad = payLoad;
        if( parent != null) {
            parent.addChild( this);
        }
    }
    
    public void addValue( String key,  JsonNode value) {
        payLoad.set( key, value);
    }

    public void addChild( SplitPart child) {
        if( children == null) {
            children = new ArrayList<>();
        }
        children.add( child);
    }
    
    public String getNodePath() {
        SplitPart part = this;
        String result = "";
        while( part != null ) {
            result = FilenameUtils.concat( part.getRelativeNodePath(), result);
            part = part.getParent();
        }
        return result;
    }
    
    public List<SplitPart> getChildren() {
        return this.children;
    }

    public String getRelativeNodePath() {
        return relativeNodePath;
    }

    public void setRelativeNodePath(String relativeNodePath) {
        this.relativeNodePath = relativeNodePath;
    }

    public SplitPart getParent() {
        return parent;
    }

    public void setParent(SplitPart parent) {
        this.parent = parent;
    }
}
