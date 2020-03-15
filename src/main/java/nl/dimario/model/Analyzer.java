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


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import nl.dimario.Constants;

import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class Analyzer implements Constants {

    private static final int STOPSPLITLEVEL = 3;

    private boolean addDefCon;
    private DefaultMutableTreeNode treeRoot = null;
    private SplitInfo splitInfoRoot = null;

    public Analyzer() {
        addDefCon = false;
    }

    private JsonNode stripDefinitionsConfig( JsonNode node) {
        JsonNode sub = node.get( DEFINITIONS);
        if( sub != null) {
            JsonNode subsub = sub.get( CONFIG);
            if( subsub != null) {
                addDefCon = true;
                return subsub;
            }
        }
        addDefCon = false;
        return node;
    }

    private void preprocessArrays( JsonNode node, ObjectMapper mapper) {

        if( node.isObject()) {
            Iterator<Map.Entry<String,JsonNode>> fields = node.fields();
            while(fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();
                JsonNode value = field.getValue();
                if( value.isArray()) {
                    JsonNode asString = convertArray( mapper, (ArrayNode) value);
                    field.setValue( asString);
                } else if( value.isObject()) {
                    preprocessArrays( value, mapper);
                }
            }
        }
    }

    /**
     * NOTE preprocessing arrays turns them into String representations.
     * Here, we add single quotes around the array elements (we presume
     * that the array elements are simple strings).
     * However, the Jackson library for unknown reasons converts those into
     * TWO consecutive single quotes when rendering a JsonNode to text.
     * For this reason,  we post-process the output in the Renderer to
     * turn them back into ONE single quote.
     */
    private ValueNode convertArray(ObjectMapper mapper, ArrayNode array) {

        StringBuilder sb = new StringBuilder();

        sb.append( "[");
        boolean addComma = false;
        for( int i = 0; i < array.size(); i++) {
            if( addComma) {
                sb.append( ", ");
            }
            addComma = true;
            String item = array.get(i).asText();
            sb.append( "'");
            sb.append( item);
            sb.append( "'");
        }
        sb.append( "]");
        ValueNode valueNode = mapper.convertValue( sb, ValueNode.class);
        return valueNode;
    }


    public DefaultMutableTreeNode makeJtree( JsonNode documentNode) {

        ObjectMapper mapper = Mapper.getMapper();

        // Convert mixin arrays to String representation
        preprocessArrays( documentNode, mapper);

        // Add invisible root wrapper while stripping definitions/configuration nodes
        JsonNode firstNode = stripDefinitionsConfig( documentNode);
        ObjectNode wrapper = mapper.createObjectNode();
        wrapper.set( DOCUMENTROOT, firstNode);
        analyseJtreeRecurse( null, wrapper);
        linkSplitInfo( this.treeRoot, this.splitInfoRoot);
        setDefaults( this.splitInfoRoot);
        return this.treeRoot;
    }

    private void analyseJtreeRecurse( DefaultMutableTreeNode treeNode, JsonNode jsonNode) {
        if( jsonNode.isArray() || jsonNode.isValueNode()) {
            return;
        }
        Iterator<Map.Entry<String,JsonNode>> fields = jsonNode.fields();
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode value = field.getValue();
            if( key.charAt(0) == '/') {
                // Start new node then recurse
                // Create split part info object and stuff it in new treenode
                SplitInfo splitInfo = new SplitInfo( key, value);
                // Keep track of the root SplitInfo
                if( splitInfoRoot == null) {
                    splitInfoRoot = splitInfo;
                }
                DefaultMutableTreeNode childNode;
                if (treeNode == null) {
                    treeRoot = new DefaultMutableTreeNode(key);
                    childNode = treeRoot;
                } else {
                    childNode = new DefaultMutableTreeNode(key);
                    treeNode.add(childNode);
                }
                childNode.setUserObject( splitInfo);
                analyseJtreeRecurse( childNode, value);

            } else if( ! value.isArray()) {
                analyseJtreeRecurse( treeNode, value);
            }
        }
    }

    private void linkSplitInfo( DefaultMutableTreeNode treeNode, SplitInfo splitInfo) {

        if( treeNode.isLeaf()) {
            return;
        }

        Enumeration<TreeNode> children = treeNode.children();
        while( children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            SplitInfo childInfo = (SplitInfo) child.getUserObject();
            splitInfo.addChild( childInfo);
            linkSplitInfo( child, childInfo);
        }
    }

    private void setDefaults( SplitInfo root) {
        WizardUtil.clearSeparateChildNodes( root);
        WizardUtil.setSeparateChildNodesByLevel( root, 0, STOPSPLITLEVEL);
        WizardUtil.setAddDefcon( root, this.addDefCon);
    }
}
