package nl.dimario.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.fasterxml.jackson.databind.JsonNode;

import nl.dimario.Constants;

public class SplitInfo implements Constants {

    private String nodeSegment;
    private String dirSegment;
    private String nodeType;
    private JsonNode jsonNode;
    private boolean addDefCon;
    private boolean stopSplit;

    private List<SplitInfo> children;
    private SplitInfo parent;

    public SplitInfo( String nodeSegment, JsonNode jsonNode) {
        this.jsonNode = jsonNode;
        this.nodeSegment = nodeSegment;
        int pos = nodeSegment.lastIndexOf(":");
        if( pos > -1) {
            this.setDirSegment( nodeSegment.substring( pos+1));
        } else {
            this.setDirSegment( nodeSegment);
        }
        JsonNode primaryTypeNode = jsonNode.get( PRIMARYTYPE);
        if( primaryTypeNode != null) {
            this.setNodeType( primaryTypeNode.textValue());
        } else {
            this.setNodeType( "unknown node type");
        }
    }

    public void addChild( SplitInfo child) {
        if( children == null) {
            children = new ArrayList<>();
        }
        children.add( child);
        child.setParent( this);
    }

    public String getNodePath() {
        String result = this.nodeSegment;
        if( parent != null) {
            String parentSegment = parent.getNodePath();
            String mySegment = this.nodeSegment;
            result = parentSegment + mySegment;
        }
        return result;
    }

    public String getFilePath() {
        return getFileSegments() + ".yaml";
    }

    private String getFileSegments() {
        String result = this.dirSegment;
        if( parent != null) {
            result = FilenameUtils.concat( parent.getFileSegments(), dirSegment);
        }
        return result;
    }

    public String getDisplayLabel() {
        return String.format( "%s = %s", nodeSegment, nodeType);
    }

    public String getNodeSegment() {
        return nodeSegment;
    }

    public void setNodeSegment(String nodeSegment) {
        this.nodeSegment = nodeSegment;
    }

    public String getDirSegment() {
        return dirSegment;
    }

    public void setDirSegment(String dirSegment) {
        if( dirSegment.startsWith( "/")) {
            dirSegment =dirSegment.substring( 1);
        }
        this.dirSegment = dirSegment;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public JsonNode getJsonNode() {
        return jsonNode;
    }

    public void setJsonNode(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public List<SplitInfo> getChildren() {
        return children;
    }

    public SplitInfo getParent() {
        return parent;
    }

    public void setParent(SplitInfo parent) {
        this.parent = parent;
    }

    public boolean isAddDefCon() {
        return addDefCon;
    }

    public void setAddDefCon(boolean addDefCon) {
        this.addDefCon = addDefCon;
    }

    public boolean isStopSplit() {
        return stopSplit;
    }

    public void setStopSplit(boolean stopSplit) {
        this.stopSplit = stopSplit;
    }


}
