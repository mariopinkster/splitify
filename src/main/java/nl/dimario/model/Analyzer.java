package nl.dimario.model;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import nl.dimario.Constants;

public class Analyzer implements Constants {

    private static final int STOPSPLITLEVEL = 3;

    private boolean addDefCon;
    private DefaultMutableTreeNode treeRoot = null;
    private SplitInfo splitInfoRoot = null;

    public DefaultMutableTreeNode makeTree(Map documentNode) {

        // Add invisible root wrapper while stripping definitions/configuration nodes
        Map firstNode = stripDefinitionsConfig(documentNode);
        Map wrapper = new LinkedHashMap();
        wrapper.put(DOCUMENTROOT, firstNode);
        analyseTreeRecurse(null, wrapper);
        linkSplitInfo(this.treeRoot, this.splitInfoRoot);
        setDefaults(this.splitInfoRoot);
        return this.treeRoot;
    }

    private Map stripDefinitionsConfig( Map node) {
        Object sub = node.get( DEFINITIONS);
        if( sub != null && sub instanceof Map) {
            Map subMap = (Map) sub;
            Object subsub = subMap.get( CONFIG);
            if( subsub != null && subsub instanceof Map) {
                addDefCon = true;
                return (Map) subsub;
            }
        }
        addDefCon = false;
        return node;
    }

    private void analyseTreeRecurse( DefaultMutableTreeNode treeNode, Object object) {
        if( ! (object instanceof LinkedHashMap)) {
            return;
        }
        LinkedHashMap<String,Object> map = (LinkedHashMap) object;
        for( Map.Entry<String, Object> field: map.entrySet()) {

            String key = field.getKey();
            Object value = field.getValue();
            if( key.charAt(0) == '/') {
                // Start new node then recurse
                // Create split part info object and stuff it in new treenode
                SplitInfo splitInfo = new SplitInfo( key, (LinkedHashMap<String, Object>) value);
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
                analyseTreeRecurse( childNode, value);

            } else if( value instanceof LinkedHashMap) {
                analyseTreeRecurse( treeNode, value);
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
        WizardUtil.setSeparateChildNodes( root, false);
        WizardUtil.setSeparateChildNodesByLevel( root, 0, STOPSPLITLEVEL);
    }

    public boolean isAddDefCon() {
        return this.addDefCon;
    }
}

