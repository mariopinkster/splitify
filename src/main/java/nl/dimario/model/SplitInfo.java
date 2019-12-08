package nl.dimario.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;

import com.fasterxml.jackson.databind.JsonNode;

import nl.dimario.Constants;
import nl.dimario.splitter.SplitPart;

public class SplitInfo implements Constants {

    private String nodeSegment;
    private String dirSegment;
    private String nodeType;
    private JsonNode jsonNode;
    private List<SplitInfo> children;
    private SplitInfo parent;

    public SplitInfo( String nodeSegment, JsonNode jsonNode) {
        this.jsonNode = jsonNode;
        this.nodeSegment = nodeSegment;
        int pos = nodeSegment.lastIndexOf(":");
        if( pos > -1) {
            this.dirSegment = nodeSegment.substring( pos+1);
        } else {
            this.dirSegment = nodeSegment;
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
    }}
