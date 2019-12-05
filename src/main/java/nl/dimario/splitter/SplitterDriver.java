package nl.dimario.splitter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 *
 */
public class SplitterDriver
{
//    public static void main( String[] args ) {
//
//        try {
//            ObjectMapper objectMapper = configureMapper();
//            ObjectNode root = (ObjectNode) objectMapper.readTree( 
////                    new File("src/test/resources/ethical-investments.yaml"));
//                    new File("src/test/resources/app.yaml"));
//            Splitter splitter = new Splitter( objectMapper, 3);            
//            SplitPart splitted =  splitter.split(root);
//            List<SplitPart> list = splitted.getChildren();
//            splitted.write( objectMapper);
//            
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    
    private static ObjectMapper configureMapper() {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.configure( YAMLGenerator.Feature.MINIMIZE_QUOTES, true);
        yamlFactory.configure( YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
        ObjectMapper mapper = new ObjectMapper( yamlFactory);
        mapper.configure( SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }
    
    public void runSplitter( int maxLevel, String inputPath)  {
        try {
            ObjectMapper objectMapper = configureMapper();
            ObjectNode root = (ObjectNode) objectMapper.readTree( 
                    new File(inputPath));
            Splitter splitter = new Splitter( objectMapper, maxLevel);            
            SplitPart splitted =  splitter.split(root);
            List<SplitPart> list = splitted.getChildren();
            splitted.write( objectMapper);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
