/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dimario.splitter;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author mbp
 */
public class PathTranslation {
    
    private static Map<String, String> translations; 
    static {
        translations = new HashMap<>();
        translations.put( "hst:hst", "hst");
        translations.put( "hst:configurations", "configurations");
        translations.put( "hst:workspace", "workspace");
        translations.put( "hst:sitemap", "sitemap");
    }
    
    public static String translatedFilePath( String baseOutputPath, String pathString) {
        String segments[] = pathString.split("/");
        String result = "";
        for( int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            String translated = translations.get( segment);
            if( translated != null) {
                segment = translated;
            }
            result = FilenameUtils.concat(result, segment);
        }
        return FilenameUtils.concat( baseOutputPath, result);
    }    
}
