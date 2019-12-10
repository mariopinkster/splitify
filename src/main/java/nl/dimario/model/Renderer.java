package nl.dimario.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.dimario.Constants;

public class Renderer implements Constants  {

    public String preview( SplitInfo splitInfo) {

        ObjectMapper mapper = Mapper.getMapper();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {

            SequenceWriter sw = mapper.writerWithDefaultPrettyPrinter().writeValues( bos);
            String nodePath = splitInfo.getNodePath();

            JsonNode renderThis = splitInfo.getJsonNode();
            if( splitInfo.isSeparateChildNodes()) {
                renderThis = stripStructures( renderThis);
            }

            ObjectNode wrapper = mapper.createObjectNode();
            if( splitInfo.isAddDefCon()) {
                ObjectNode config = mapper.createObjectNode();
                config.set( nodePath, renderThis);
                ObjectNode definitions = mapper.createObjectNode();
                definitions.set( CONFIG, config);
                wrapper.set( DEFINITIONS, definitions);
            } else {
                wrapper.set( nodePath, renderThis);
            }
            sw.write(wrapper);
            return postProcess(bos);

        } catch (Exception x) {
            return "ERROR: " + x.getMessage();
        }
    }

    private String postProcess( ByteArrayOutputStream bos) throws IOException {

        String data = bos.toString( StandardCharsets.UTF_8);
        data = data.replace( "[${", "['${");
        data = data.replace( "}]", "}']");
        data = data.replace( "'[", "[");
        data = data.replace( "]'", "]");
        return data;

//        String filePath = PathTranslation.translatedFilePath( "output", getNodePath());
//        filePath  = filePath + ".yaml";
//        String outDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
//        (new File(outDir)).mkdirs();
//        try( FileOutputStream fos = new FileOutputStream( new File( filePath))) {
//            IOUtils.write( data, fos, StandardCharsets.UTF_8);
//        }
    }

    private ObjectNode stripStructures(JsonNode node) {

        ObjectNode result = Mapper.getMapper().createObjectNode();
        Iterator<Map.Entry<String,JsonNode>> fields = node.fields();
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            JsonNode value = field.getValue();
            if( value.isValueNode()) {
                result.set( key, value);
            }
        }
        return result;
    }
}
