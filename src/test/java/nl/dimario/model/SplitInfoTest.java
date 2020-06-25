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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import nl.dimario.Constants;

public class SplitInfoTest {

    private static String NODESEGMENT = "/prefix:directory";
    private static String DIRSEGMENT ="directory";
    private static String TESTNODETYPE = "hippo:excavator";

    @Test
    public void testConstructor() {

        LinkedHashMap<String,Object> node = new LinkedHashMap<>();
        SplitInfo test  = new SplitInfo( NODESEGMENT, node);
        assertEquals( "unknown node type", test.getNodeType());
        assertEquals(DIRSEGMENT, test.getDirSegment());

        node.put( "jcr:primaryType", TESTNODETYPE);
        test = new SplitInfo( NODESEGMENT, node);
        assertEquals( "hippo:excavator", test.getNodeType());
    }

    @Test
    public void testSetDirSegment() {

        LinkedHashMap<String,Object> node = new LinkedHashMap<>();
        SplitInfo test  = new SplitInfo( NODESEGMENT, node);

        test.setDirSegment( "/simple");
        assertEquals( "simple", test.getDirSegment());

        test.setDirSegment( "/withprefix:simple");
        assertEquals( "simple", test.getDirSegment());

        test.setDirSegment( "noslash");
        assertEquals( "noslash", test.getDirSegment());

        test.setDirSegment(Constants.DOCUMENTROOT);
        assertEquals( "", test.getDirSegment());

        test.setDirSegment( "/abc:first/def:second/third/xyz:fourth");
        if (SystemUtils.IS_OS_WINDOWS) {
            assertEquals("first\\second\\third\\fourth", test.getDirSegment());
        } else {
            assertEquals("first/second/third/fourth", test.getDirSegment());
        }
    }

    @Test
    public void testAddChild() {

        LinkedHashMap<String,Object> a = new LinkedHashMap<>();
        SplitInfo parent = new SplitInfo( "/a:Aaa", a);

        assertNull( parent.getChildren());

        LinkedHashMap<String,Object> b = new LinkedHashMap<>();
        SplitInfo child = new SplitInfo( "/b:Bbb", b);

        parent.addChild( child);
        assertTrue( parent.getChildren() != null);
        assertEquals( parent, child.getParent());
        assertEquals( child, parent.getChildren().get(0));
    }

    private void createTree( SplitInfo node, int level) {

        for( int i=0; i < level; i++) {
            LinkedHashMap<String,Object> localNode = new LinkedHashMap<>();
            String nodesegment = String.format( "/%d:%d-%d", level, i, level);
            SplitInfo splitInfo = new SplitInfo( nodesegment, localNode);
            node.addChild( splitInfo);
            createTree( splitInfo, level-1);
        }
    }

    @Test
    public void testNodeAndFilePath() {

        LinkedHashMap<String,Object> rootMap = new LinkedHashMap<>();
        SplitInfo root = new SplitInfo(Constants.DOCUMENTROOT, rootMap);
        createTree(root, 3);

        String rootFilePath = root.getFilePath();
        assertEquals(".yaml", rootFilePath);

        // Test Random elements
        SplitInfo si_2 = root.getChildren().get(2);
        assertEquals("/3:2-3", si_2.getNodePath());
        assertEquals("2-3.yaml", si_2.getFilePath());

        SplitInfo si_1 = root.getChildren().get(1);
        SplitInfo si_1_0 = si_1.getChildren().get(0);
        assertEquals( "/3:1-3/2:0-2", si_1_0.getNodePath());
        if (SystemUtils.IS_OS_WINDOWS) {
            assertEquals( "1-3\\0-2.yaml", si_1_0.getFilePath());
        } else {
            assertEquals("1-3/0-2.yaml", si_1_0.getFilePath());
        }
    }
}
