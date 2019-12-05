package nl.dimario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.module.Configuration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {

    private static String CONFIGFILENAME = "splitify.properties";
    private static String USERHOMEDIR = "user.home";
    private static String MAXLEVEL = "maxLevel";
    
    private Properties properties;
    
    private int maxLevel = 3;
    
    public int getMaxLevel() {
        return this.maxLevel;
    }

    public void setMaxLevel( int maxLevel) {
        this.maxLevel = maxLevel;
    }
    
    private void valuesToProperties() {
        properties.setProperty(MAXLEVEL, Integer.toString( this.maxLevel));    
    }
    
    private void valuesFromProperties() {
        maxLevel = Integer.parseInt( properties.getProperty( MAXLEVEL));
    }
    
    public void load() {
        this.properties = new Properties();
        String homedir = System.getProperty( USERHOMEDIR);
        File configFile = new File( homedir, CONFIGFILENAME);
        try( FileInputStream fis = new FileInputStream( configFile)) {
            properties.load(fis);
            valuesFromProperties();            
        } catch (IOException ex) {
            // configfile does not yet exist or can't be found.
            // Set default vallues to properties.
            valuesToProperties();
        }
    }
    
    public void save() {
        if( properties == null) {
            properties = new Properties();
        }
        valuesToProperties();
        String homedir = System.getProperty( USERHOMEDIR);
        File configFile = new File( homedir, CONFIGFILENAME);
        try( FileOutputStream fos = new FileOutputStream( configFile)) {
            properties.store(fos, "This is a comment");
        } catch (IOException ex) {
            // TODO  set status
        }
    }    
}
