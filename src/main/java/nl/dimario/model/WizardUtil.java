package nl.dimario.model;


import com.fasterxml.jackson.databind.JsonNode;

import javax.swing.*;
import java.util.*;

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

    public static void clearSeparateChildNodes(SplitInfo root) {

        root.setSeparateChildNodes(true);
        if (root.getChildren() != null) {
            for (SplitInfo child : root.getChildren()) {
                clearSeparateChildNodes(child);
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

        String thisType = root.getNodeType();
        root.setSeparateChildNodes(true);
        if (type.equals(thisType)) {
            return;
        }
        if (root.getChildren() != null) {
            for (SplitInfo child : root.getChildren()) {
                setSeparateChildNodesByType(child, type);
            }
        }
    }

    public static void setAddDefcon(SplitInfo root, boolean newDefcon) {

        root.setAddDefCon( newDefcon);
        if (root.getChildren() != null) {
            for (SplitInfo child : root.getChildren()) {
                setAddDefcon(child, newDefcon);
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
