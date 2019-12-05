package nl.dimario.analyse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import nl.dimario.Constants;

import java.util.*;

public class Analyzer implements Constants {

    private boolean isDefCon;
    private Map<String, PathSegment> pathSegments;

    public Map<String, NodeTypeOptions> getNodeTypeOptions() {
        return nodeTypeOptions;
    }

    private Map<String, NodeTypeOptions> nodeTypeOptions;

    public Analyzer() {
        pathSegments = new HashMap<>();
        nodeTypeOptions = new HashMap<>();
        isDefCon = false;
    }

    public void analyse( JsonNode node) {
        node = stripDefinitionsConfig( node);
        analyseRecurse( node, 0);
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
    private void analyseRecurse(JsonNode node, int level) {

        if( node.isValueNode()) {
            return;
        }
        if( node.isArray()) {
            return;
        }
        Iterator<Map.Entry<String,JsonNode>> fields = node.fields();
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode value = field.getValue();
            if( key.charAt(0) == '/') {
                analyzeNodePath( key, level);
                analyseRecurse( value, level + 1);
            } else if ( PRIMARYTYPE.equals( key)) {
                analyzeNodeType( value, level);
            } else {
                analyseRecurse( value, level + 1);
            }
        }
    }

    private void analyzeNodeType( JsonNode value, int level) {
        String nodeType = ((ValueNode) value).textValue();
        if( ! nodeTypeOptions.containsKey( nodeType)) {
            NodeTypeOptions options = new NodeTypeOptions( nodeType, level);
            options.setStopRecurse( level > DEFAULTMAXLEVEL);
            options.setDefCon( this.isDefCon);
            nodeTypeOptions.put( nodeType, options);
        }
    }


    private void analyzeNodePath( String nodePath, int level) {
        String segments[] = nodePath.split("/");
        for( int i = 1; i<segments.length; i++) {
            String key = segments[i];
            if( ! pathSegments.containsKey( key)) {
                pathSegments.put( key, new PathSegment( level, key));
            }
        }
    }
}
