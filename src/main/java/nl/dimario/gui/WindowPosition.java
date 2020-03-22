package nl.dimario.gui;

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

import nl.dimario.Constants;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class WindowPosition implements Constants {

    static private final String TOP = "window.top";
    static private final String LEFT = "window.left";
    static private final String HEIGHT = "window.height";
    static private final String WIDTH= "window.width";

    private int left = 50;
    private int top = 50;
    private int width = 523;
    private int height = 367;

    public void saveWindowDimension( JFrame frame) {
        this.height = frame.getHeight();
        this.width = frame.getWidth();
        this.top = frame.getY();
        this.left = frame.getX();
        save();
    }

    public void loadWindowDimension( JFrame frame) {
        load();
        frame.setSize( this.width, this.height);
        frame.setLocation( this.left, this.top);
    }

    public void load() {
        Properties properties = new Properties();
        String homedir = System.getProperty( USERHOMEDIR);
        File configFile = new File( homedir, CONFIGFILENAME);
        try( FileInputStream fis = new FileInputStream( configFile)) {
            properties.load(fis);
            this.top = Integer.parseInt( properties.getProperty( TOP));
            this.left = Integer.parseInt( properties.getProperty( LEFT));
            this.width = Integer.parseInt( properties.getProperty( WIDTH));
            this.height = Integer.parseInt( properties.getProperty( HEIGHT));
        } catch (IOException ex) {
            // TODO  set status
        }
    }
    
    public void save() {

        Properties properties = new Properties();
        properties.setProperty( TOP, Integer.toString( this.top));
        properties.setProperty( LEFT, Integer.toString( this.left));
        properties.setProperty( WIDTH, Integer.toString( this.width));
        properties.setProperty( HEIGHT, Integer.toString( this.height));

        String homedir = System.getProperty( USERHOMEDIR);
        File configFile = new File( homedir, CONFIGFILENAME);
        try( FileOutputStream fos = new FileOutputStream( configFile)) {
            properties.store(fos, "This is a comment");
        } catch (IOException ex) {
            // TODO  set status
        }
    }    
}
