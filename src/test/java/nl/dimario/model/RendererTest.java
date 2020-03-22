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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RendererTest {

    @Test
    public void testPostProcess() {

        OutputOptions options = new OutputOptions();

        Renderer r = new Renderer();
        String input = "wicket.id: ${cluster.id}.left";
        options.setAddQuotesToPlaceholder(true);
        assertEquals( "wicket.id: ${cluster.id}.left", r.postProcess( input, options));

        options.setRemoveExtraQuotes(true);
        input =  "jcr:mixinTypes: [''editor:editable'', ''mix:referenceable'']";
        assertEquals( "jcr:mixinTypes: ['editor:editable', 'mix:referenceable']", r.postProcess( input, options));

        options.setRemoveQuotesFromArray(true);
        input =  "hst:parametervalues: [''${1}'', ''${2}'']";
        assertEquals( "hst:parametervalues: ['${1}', '${2}']", r.postProcess( input, options));
    }

    @Test
    public void testStripStructure() {

        ObjectMapper m = Mapper.getMapper();
        ObjectNode input = m.createObjectNode();
        input.put( "AA", "aa");
        ObjectNode struct = m.createObjectNode();
        input.set( "STRUCT", struct);
        input.put( "BBB", new Integer( 12));
        ArrayNode array =m.createArrayNode();
        input.set( "ARRAY", array);
        input.put( "CCCC", "cccc");

        Renderer r = new Renderer();
        ObjectNode result = r.stripStructures( input);

        assertFalse( result.has( "STRUCT"));
        assertFalse( result.has( "ARRAY"));
        assertTrue( result.has( "AA"));
        assertTrue( result.has( "BBB"));
        assertTrue( result.has( "CCCC"));
    }

}
