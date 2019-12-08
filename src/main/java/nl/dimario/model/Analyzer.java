package nl.dimario.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import nl.dimario.Constants;

import java.util.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class Analyzer implements Constants {

    private boolean isDefCon;
    private DefaultMutableTreeNode jRoot = null;
    private SplitInfo splitInfoRoot = null;

    public Analyzer() {
        isDefCon = false;
    }

    private JsonNode stripDefinitionsConfig( JsonNode node) {
        JsonNode sub = node.get( DEFINITIONS);
        if( sub != null) {
            JsonNode subsub = sub.get( CONFIG);
            if( subsub != null) {
                isDefCon = true;
                return subsub;
            }
        }
        isDefCon = false;
        return node;
    }

    public DefaultMutableTreeNode makeJtree( JsonNode rootNode) {
        rootNode = stripDefinitionsConfig( rootNode);
        analyseJtreeRecurse( null, rootNode);
        linkSplitInfo( this.jRoot, this.splitInfoRoot);
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
                SplitInfo splitInfo = new SplitInfo( key, jsonNode);
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
}
