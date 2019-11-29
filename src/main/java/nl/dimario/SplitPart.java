package nl.dimario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class SplitPart {

    private String relativeNodePath;
    private SplitPart parent;
    private ObjectNode payLoad;
    List<SplitPart> children;

    public SplitPart( SplitPart parent, String relativeNodePath, ObjectNode payLoad) {
        this.parent = parent;
        this.relativeNodePath =  relativeNodePath;
        this.payLoad = payLoad;
        if( parent != null) {
            parent.addChild( this);
        }
    }
    
    public void addValue( String key,  JsonNode value) {
        payLoad.set( key, value);
    }

    public void addChild( SplitPart child) {
        if( children == null) {
            children = new ArrayList<>();
        }
        children.add( child);
    }
    
    public String getNodePath() {
        String result = this.relativeNodePath;
        if( parent != null) {
            result = parent.getNodePath() + relativeNodePath;
        }
        return result;
    }
    
    public void write( ObjectMapper objectMapper) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        SequenceWriter sw = objectMapper.writerWithDefaultPrettyPrinter().writeValues( bos);
        ObjectNode wrapper = objectMapper.createObjectNode();
        String nodePath = getNodePath();
        wrapper.set( nodePath, payLoad);
        sw.write( wrapper);
        postProcessAndSave(bos);
        
        if( children != null) {
            for( SplitPart child:  children) {
                child.write( objectMapper);
            }
        }
    }
    
    private void postProcessAndSave( ByteArrayOutputStream bos) throws IOException {
        
        String data = bos.toString( StandardCharsets.UTF_8);
        data = data.replace( "[${", "['${");
        data = data.replace( "}]", "}']");
        data = data.replace( "'[", "[");
        data = data.replace( "]'", "]");
        
        String filePath = PathTranslation.translatedFilePath( "output", getNodePath());
        filePath  = filePath + ".yaml";        
        String outDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        (new File(outDir)).mkdirs();
        try( FileOutputStream fos = new FileOutputStream( new File( filePath))) {
            IOUtils.write( data, fos, StandardCharsets.UTF_8);
        }
    }
    
    public List<SplitPart> getChildren() {
        return this.children;
    }

//    public String getRelativeNodePath() {
//        return relativeNodePath;
//    }

    public void setRelativeNodePath(String relativeNodePath) {
        this.relativeNodePath = relativeNodePath;
    }

    public SplitPart getParent() {
        return parent;
    }

//    public void setParent(SplitPart parent) {
//        this.parent = parent;
//    }

    public ObjectNode getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(ObjectNode payLoad) {
        this.payLoad = payLoad;
    }
    
    
}
