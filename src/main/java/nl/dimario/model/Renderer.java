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


import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.yaml.snakeyaml.Yaml;

import nl.dimario.Constants;

public class Renderer implements Constants  {

    public String preview(SplitInfo splitInfo, OutputOptions outputOptions) {

        try {

            String nodePath = splitInfo.getNodePath();

            LinkedHashMap<String, Object> renderThis = splitInfo.getDataMap();
            if( splitInfo.isSeparateChildNodes()) {
                renderThis = stripStructures( renderThis);
            }

            LinkedHashMap<String, Object> wrapper = new LinkedHashMap<>();
            if( outputOptions.isAddDefinitionsConfig() &&  !splitInfo.isIncludedInParent()) {
                LinkedHashMap<String, Object> config = new LinkedHashMap<>();
                config.put(nodePath, renderThis);
                LinkedHashMap<String, Object> definitions = new LinkedHashMap<>();
                definitions.put(CONFIG, config);
                wrapper.put(DEFINITIONS, definitions);
            } else {
                wrapper.put( nodePath, renderThis);
            }

            Yaml yaml = new Yaml();
            StringWriter sw = new StringWriter();
            yaml.dump(wrapper, sw);
            String data = sw.toString();
            if(outputOptions.isRemoveUuids()) {
                data = removeAllUUids(data);
            }
            return postProcess( data);

        } catch (Exception x) {
            return "ERROR: " + x.getMessage();
        }
    }

    /**
     * Post process the output:
     * Remove double quotes around arrays (but not around innocent text)
     */
    protected String postProcess( String data) {
        StringBuffer sb  = new StringBuffer(data.length());
        Pattern array = Pattern.compile( "^(\\s+\\S+: )(\"\\[)(.*)(]\")", Pattern.MULTILINE);
        Matcher arrayMatcher = array.matcher(data);
        while(arrayMatcher.find()) {
            String keepThis = arrayMatcher.group(1);
            String keepThisToo = arrayMatcher.group(3);
            arrayMatcher.appendReplacement(sb, keepThis + "[" + keepThisToo + "]");
        }
        arrayMatcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Post process the output:
     * Remove all lines containing a "jcr:uuid" value
     */
    protected String removeAllUUids(String data) {
        StringBuffer sb  = new StringBuffer(data.length());
        Pattern uuid = Pattern.compile( "\\s+jcr:uuid:.*$", Pattern.MULTILINE);
        Matcher uuidMatcher = uuid.matcher(data);
        while(uuidMatcher.find()) {
            uuidMatcher.appendReplacement(sb, "");
        }
        uuidMatcher.appendTail(sb);
        return sb.toString();
    }

    protected LinkedHashMap<String, Object> stripStructures(LinkedHashMap<String, Object> node) {

        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for( Map.Entry<String,Object> field : node.entrySet()) {
            String key = field.getKey();
            Object value = field.getValue();
            if( ! (value instanceof LinkedHashMap)) {
                result.put( key, value);
            }
        }
        return result;
    }

}
