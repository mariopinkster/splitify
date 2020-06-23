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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.dimario.Constants;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Renderer implements Constants  {

    public String preview(SplitInfo splitInfo, OutputOptions outputOptions) {

        ObjectMapper mapper = Mapper.getMapper();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {

            SequenceWriter sw = mapper.writer().writeValues( bos);
            String nodePath = splitInfo.getNodePath();

            JsonNode renderThis = splitInfo.getJsonNode();
            if( splitInfo.isSeparateChildNodes()) {
                renderThis = stripStructures( renderThis);
            }

            ObjectNode wrapper = mapper.createObjectNode();
            if( outputOptions.isAddDefinitionsConfig() &&  !splitInfo.isIncludedInParent()) {
                ObjectNode config = mapper.createObjectNode();
                config.set(nodePath, renderThis);
                ObjectNode definitions = mapper.createObjectNode();
                definitions.set(CONFIG, config);
                wrapper.set(DEFINITIONS, definitions);
            } else {
                wrapper.set( nodePath, renderThis);
            }
            sw.write(wrapper);
            String data = bos.toString( StandardCharsets.UTF_8);
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

    protected ObjectNode stripStructures(JsonNode node) {

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
