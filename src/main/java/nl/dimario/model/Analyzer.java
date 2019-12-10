package nl.dimario.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import nl.dimario.Constants;

import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class Analyzer implements Constants {

    private static final int STOPSPLITLEVEL = 3;

    private boolean addDefCon;
    private DefaultMutableTreeNode jRoot = null;
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


    public DefaultMutableTreeNode makeJtree( JsonNode rootNode) {
        rootNode = stripDefinitionsConfig( rootNode);
        ObjectMapper mapper = Mapper.getMapper();
        preprocessArrays( rootNode, mapper);
        analyseJtreeRecurse( null, rootNode);
        linkSplitInfo( this.jRoot, this.splitInfoRoot);
        setDefaults( this.splitInfoRoot, 0);
        return this.jRoot;
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
                    jRoot = new DefaultMutableTreeNode(key);
                    childNode = jRoot;
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

    private void setDefaults( SplitInfo splitInfo, int level) {

        splitInfo.setSeparateChildNodes( level < STOPSPLITLEVEL);
        splitInfo.setAddDefCon( this.addDefCon);
        if( splitInfo.getChildren() != null) {
            for( SplitInfo child: splitInfo.getChildren()) {
                setDefaults( child, level + 1);
            }
        }
    }
}
