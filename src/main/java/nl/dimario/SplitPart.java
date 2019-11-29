package nl.dimario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

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
        System.out.println( getNodePath());
        String filePath = PathTranslation.translatedFilePath( "output", getNodePath());
        filePath  = filePath + ".yaml";
        System.out.println( filePath);
        
        String outDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        (new File(outDir)).mkdirs();
        FileOutputStream fos = new FileOutputStream( new File( filePath));
        SequenceWriter sw = objectMapper.writerWithDefaultPrettyPrinter().writeValues( fos);
        sw.write( this.payLoad);
        if( children != null) {
            for( SplitPart child:  children) {
                child.write( objectMapper);
            }
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
