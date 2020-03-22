package nl.dimario.model;
/**
 *     This file is part of Splitify
 *
 *     Splitify is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, version 3 of the License.
 *
 *     Splitify is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */


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
    private boolean separateChildNodes;

    private List<SplitInfo> children;
    private SplitInfo parent;

    public SplitInfo( String nodeSegment, JsonNode jsonNode) {
        this.jsonNode = jsonNode;
        this.nodeSegment = nodeSegment;
        setDirSegment( nodeSegment);
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
        if( DOCUMENTROOT.equals( this.nodeSegment)) {
            result = "";
        }
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

        if( DOCUMENTROOT.equals( dirSegment)) {
            this.dirSegment = "";
            return;
        }
        String[] segments = dirSegment.split( "/");
        String setValue = "";
        for( int i = 0; i < segments.length; i++) {
            String thisSegment = segments[i];
            if( thisSegment == null) {
                continue;
            }
            int pos = thisSegment.lastIndexOf( ":");
            if( pos > -1) {
                thisSegment = thisSegment.substring( pos+1);
            }
            setValue = FilenameUtils.concat( setValue, thisSegment);
        }
        this.dirSegment = setValue;
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

    public boolean isSeparateChildNodes() {
        return separateChildNodes;
    }

    public void setSeparateChildNodes(boolean separateChildNodes) {
        this.separateChildNodes = separateChildNodes;
    }
}
