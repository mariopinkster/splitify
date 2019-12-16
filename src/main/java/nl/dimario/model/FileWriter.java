package nl.dimario.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.crypto.dom.DOMCryptoContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import nl.dimario.Constants;

public class FileWriter {

    private String baseDir;

    public FileWriter( String fullFileName) {
        this.baseDir = FilenameUtils.getFullPath( fullFileName);
    }

    public void writeOne( SplitInfo splitInfo, Renderer renderer) throws IOException {
        String filePath = splitInfo.getFilePath();
        filePath = FilenameUtils.concat( baseDir, filePath);
        String outDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        (new File(outDir)).mkdirs();
        try( FileOutputStream fos = new FileOutputStream( new File( filePath))) {
            String data = renderer.preview( splitInfo);
            IOUtils.write( data, fos, StandardCharsets.UTF_8);
        }
    }

    public void writeAll( SplitInfo splitInfo, Renderer renderer) throws IOException {

        if (! (Constants.DOCUMENTROOT.equals( splitInfo.getNodeSegment()))) {
            writeOne(splitInfo, renderer);
        }
        if( splitInfo.getChildren() != null && splitInfo.isSeparateChildNodes()) {
            for( SplitInfo child: splitInfo.getChildren()) {
                writeAll( child, renderer);
            }
        }
    }
}
