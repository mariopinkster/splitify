package nl.dimario.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;

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

public class WizardUtil {

    public static void setSeparateChildNodes(SplitInfo root, boolean newValue) {

        root.setSeparateChildNodes( newValue);
        if (root.getChildren() != null) {
            for (SplitInfo child : root.getChildren()) {
                setSeparateChildNodes(child, newValue);
            }
        }
    }

    public static void setSeparateChildNodesByLevel(SplitInfo root, int thisLevel, int stopLevel) {

        if (thisLevel >= stopLevel) {
            return;
        }
        root.setSeparateChildNodes(true);
        if (root.getChildren() != null) {
            for (SplitInfo child : root.getChildren()) {
                setSeparateChildNodesByLevel(child, thisLevel + 1, stopLevel);
            }
        }
    }

    public static void setSeparateChildNodesByType(SplitInfo root, String type) {

        root.setSeparateChildNodes(true);
        String thisType = root.getNodeType();
        if (type.equals(thisType)) {
            root.setSeparateChildNodes(false);
            return;
        }
        if (root.getChildren() != null) {
            for (SplitInfo child : root.getChildren()) {
                setSeparateChildNodesByType(child, type);
            }
        }
    }

    public static void setNodeTypeValues(SplitInfo root, JComboBox box) {
        Set<String> set = new HashSet<>();
        collectNodeType( root, set);
        List<String> list = new ArrayList<>( set);
        Collections.sort( list);
        for( String nodeType: list) {
            box.addItem( nodeType);
        }
    }

    private static void collectNodeType( SplitInfo root, Set<String> set) {
        set.add( root.getNodeType());
        if( root.getChildren() != null) {
            for( SplitInfo child: root.getChildren()) {
                collectNodeType( child, set);
            }
        }
    }
}
