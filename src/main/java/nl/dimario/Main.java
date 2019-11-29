package nl.dimario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args ) {

        try {
            YAMLFactory yamlFactory = new YAMLFactory();
            ObjectMapper objectMapper = new ObjectMapper( yamlFactory);
            ObjectNode root = (ObjectNode) objectMapper.readTree( new File("src/test/resources/ethical-investments.yaml"));
            Splitter splitter = new Splitter( objectMapper);            
            SplitPart splitted =  splitter.split(root);
            List<SplitPart> list = splitted.getChildren();
            splitted.write( objectMapper);

//            FileOutputStream fos = new FileOutputStream( new File( "src/test/resources/out.yaml"));
//            SequenceWriter sw = objectMapper.writerWithDefaultPrettyPrinter().writeValues( fos);
//            sw.write( root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
