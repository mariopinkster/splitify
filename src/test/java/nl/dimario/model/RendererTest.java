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

import java.util.LinkedHashMap;

import org.junit.Test;

public class RendererTest {

    @Test
    public void testPostProcess() {

        OutputOptions options = new OutputOptions();

        Renderer r = new Renderer();
        String input = "wicket.id: ${cluster.id}.left";
        assertEquals( "wicket.id: ${cluster.id}.left", r.postProcess( input));

        input =  "  jcr:mixinTypes: \"['editor:editable', 'mix:referenceable']\"";
        assertEquals( "  jcr:mixinTypes: ['editor:editable', 'mix:referenceable']", r.postProcess( input));
    }

    @Test
    public void testStripStructure() {

        LinkedHashMap<String,Object> input = new LinkedHashMap<>();

        input.put( "AA", "aa");
        LinkedHashMap<String,Object> struct = new LinkedHashMap<>();
        input.put( "STRUCT", struct);
        input.put( "BBB", new Integer( 12));
        LinkedHashMap<String,Object> array = new LinkedHashMap<>();

        input.put( "ARRAY", array);
        input.put( "CCCC", "cccc");

        Renderer r = new Renderer();
        LinkedHashMap<String, Object> result = r.stripStructures( input);

        assertFalse( result.containsKey( "STRUCT"));
        assertFalse( result.containsKey( "ARRAY"));
        assertTrue( result.containsKey( "AA"));
        assertTrue( result.containsKey( "BBB"));
        assertTrue( result.containsKey( "CCCC"));
    }

}
