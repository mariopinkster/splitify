package nl.dimario.splitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.util.Iterator;
import java.util.Map;
import nl.dimario.Constants;

public class Splitter  implements Constants {
    
    private int maxLevel;
    private ObjectMapper mapper;
    

    public Splitter( ObjectMapper mapper, int maxLevel) {
        this.mapper = mapper;
        this.maxLevel = maxLevel;
    }
    
    public SplitPart split( ObjectNode node) {
        preprocessArrays(node);
        SplitPart rootSplit = new SplitPart(null, "", mapper.createObjectNode());
        return recurseSplit( rootSplit, node, 0);
    }
    
    public SplitPart recurseSplit( SplitPart currentSplit, ObjectNode node, int level) {
        
        if( node.isValueNode()) {
            throw new RuntimeException( "Unexpected ValueNode in recursion");
        }
        if( level >= maxLevel) {
            currentSplit.setPayLoad(node);
            return currentSplit;
        }
        
        Iterator<Map.Entry<String,JsonNode>> fields = node.fields();
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode value = field.getValue();
            if( value.isArray()) {
                throw new RuntimeException( "Array should have been removed by preprocessor");
            } else if( value.isValueNode()) {
                // add attribute to current split
                currentSplit.addValue( key, (ValueNode) value);
            } else {
                // It must be an object node.
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
                    recurseSplit( newSplit, (ObjectNode) value, level + 1);
                } else {
                    throw new RuntimeException( "Unxepected Situation");                }
            }
        }
        return currentSplit;
    }
    
    private void preprocessArrays( JsonNode node) {
        
        if( node.isObject()) {
            Iterator<Map.Entry<String,JsonNode>> fields = node.fields();
            while(fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();
                JsonNode value = field.getValue();
                if( value.isArray()) {
                    JsonNode asString = convertArray( (ArrayNode) value);
                    field.setValue( asString);
                } else if( value.isObject()) {
                    preprocessArrays( value);
                }
            }
        }
    }
    
    private ValueNode convertArray( ArrayNode array) {
                
        StringBuilder sb = new StringBuilder();
        
        sb.append( "[");
        boolean addComma = false;
        for( int i = 0; i < array.size(); i++) {
            if( addComma) {
                sb.append( ", ");
            }
            addComma = true;
            String item = array.get(i).asText();
            sb.append( item);
        }        
        sb.append( "]");
        ValueNode valueNode = mapper.convertValue( sb, ValueNode.class);        
        return valueNode;
    }
}
