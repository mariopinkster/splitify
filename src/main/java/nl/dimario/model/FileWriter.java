package nl.dimario.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class FileWriter {

    private String baseDir;

    public FileWriter(String baseDir) {
        this.baseDir = baseDir;
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

        writeOne( splitInfo, renderer);
        if( splitInfo.getChildren() != null) {
            for( SplitInfo child: splitInfo.getChildren()) {
                writeAll( child, renderer);
            }
        }
    }
}
