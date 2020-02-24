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
            String data = bos.toString( StandardCharsets.UTF_8);
            return postProcess( data);

        } catch (Exception x) {
            return "ERROR: " + x.getMessage();
        }
    }

    /**
     * Post process the output to remove the ideosyncrasies introduced by
     * the Jackson JsonNode to text process.
     */
    protected String postProcess( String data) {

        data = data.replace( "[${", "['${");
        data = data.replace( "}]", "}']");
        data = data.replace( "'[", "[");
        data = data.replace( "]'", "]");
        data = data.replace( "''", "'");
        return data;
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
