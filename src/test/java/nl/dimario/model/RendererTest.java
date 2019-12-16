package nl.dimario.model;

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

        Renderer r = new Renderer();
        String input = "wicket.id: ${cluster.id}.left";
        assertEquals( "wicket.id: ${cluster.id}.left", r.postProcess( input));

        input =  "jcr:mixinTypes: [''editor:editable'', ''mix:referenceable'']";
        assertEquals( "jcr:mixinTypes: ['editor:editable', 'mix:referenceable']", r.postProcess( input));

        input =  "hst:parametervalues: [''${1}'', ''${2}'']";
        assertEquals( "hst:parametervalues: ['${1}', '${2}']", r.postProcess( input));
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
