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
    private String filePath;
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

//    public void initFromPayload() {
//        String parentNodePath = "";
//        if( parent != null) {
//            parentNodePath = parent.getRelativeNodePath();
//        }
//        Iterator<Map.Entry<String, JsonNode>> fields = payLoad.fields();
//        if(fields.hasNext()) {
//            Map.Entry<String, JsonNode> entry = fields.next();
//            String relativePath = entry.getKey();
//            this.relativeNodePath = FilenameUtils.concat( parentNodePath, relativePath);
//        }
//    }
    
    public void addValue( String key,  JsonNode value) {
        payLoad.set( key, value);
    }

    public void addChild( SplitPart child) {
        if( children == null) {
            children = new ArrayList<SplitPart>();
        }
        children.add( child);
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public SplitPart getParent() {
        return parent;
    }

    public void setParent(SplitPart parent) {
        this.parent = parent;
    }
}
