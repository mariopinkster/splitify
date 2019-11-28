package nl.dimario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.util.Iterator;
import java.util.Map;

public class Splitter {

    public static int maxLevel = 3;

    private ObjectMapper mapper;
    

    public Splitter( ObjectMapper mapper) {
        this.mapper = mapper;
    }
    
    public SplitPart split( JsonNode node) {
        SplitPart rootSplit = new SplitPart(null, "", mapper.createObjectNode());
        return recurseSplit( rootSplit, node, 0);
    }
    
    public SplitPart recurseSplit( SplitPart currentSplit, JsonNode node, int level) {
        
        if( node.isValueNode()) {
            throw new RuntimeException( "Unexpected ValueNode in recursion");
        }
        
        Iterator<Map.Entry<String,JsonNode>> fields = node.fields();
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode value = field.getValue();
            if( value.isValueNode()) {
                // add attribute to current split
                currentSplit.addValue( key, (ValueNode) value);
            } else {
                // if key starts with slash start new split
                // but use root split if at level 0
                if( key.charAt(0) == '/')  {
                    SplitPart newSplit = null;
                    if( level == 0) {
                        currentSplit.setRelativeNodePath(key);
                        newSplit = currentSplit;
                    } else {
                        newSplit = new SplitPart( currentSplit, key, mapper.createObjectNode());
                    }
                    recurseSplit( newSplit, value, level + 1);
                } else if( value.isArray()) {
                    // if it is an array append the array as an attribute (for Hippo)
                    currentSplit.addValue( key, value);
                } else {
                    throw new RuntimeException( "Unxepected Situation");
                }
            }
        }
        return currentSplit;
    }
}
