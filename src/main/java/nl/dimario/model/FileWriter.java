package nl.dimario.model;
/**
 *     This file is part of Splitify
 *
 *     Splitify is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, version 3 of the License.
 *
 *     Splitify is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */


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

    public void writeOne( SplitInfo splitInfo, OutputOptions outputOptions, Renderer renderer) throws IOException {
        String filePath = splitInfo.getFilePath();
        filePath = FilenameUtils.concat( baseDir, filePath);
        String outDir = FilenameUtils.getFullPathNoEndSeparator(filePath);
        (new File(outDir)).mkdirs();
        try( FileOutputStream fos = new FileOutputStream( new File( filePath))) {
            String data = renderer.preview( splitInfo, outputOptions);
            IOUtils.write( data, fos, StandardCharsets.UTF_8);
        }
    }

    public void writeAll( SplitInfo splitInfo, OutputOptions outputOptions, Renderer renderer) throws IOException {

        if (! (Constants.DOCUMENTROOT.equals( splitInfo.getNodeSegment()))) {
            writeOne(splitInfo, outputOptions, renderer);
        }
        if( splitInfo.getChildren() != null && splitInfo.isSeparateChildNodes()) {
            for( SplitInfo child: splitInfo.getChildren()) {
                writeAll( child, outputOptions, renderer);
            }
        }
    }

}
