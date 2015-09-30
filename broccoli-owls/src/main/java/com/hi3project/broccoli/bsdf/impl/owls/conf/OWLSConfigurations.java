/*******************************************************************************
 *
 * Copyright (C) 2015 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 * Copyright (C) 2015 Alejandro Paz <alejandropl@lagostelle.com>
 *
 * This file is part of Broccoli.
 *
 * Broccoli is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Broccoli is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Broccoli. If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.hi3project.broccoli.bsdf.impl.owls.conf;

import com.hi3project.broccoli.conf.ProjProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>Description:</p>
 *  Singleton object that manages centralized information and configurations:
 * <ul>
 *  <li>logging</li>
 *  <li>access to .properties file an its contents</li>
 * </ul>
 * 
 */
public class OWLSConfigurations {
    
    private static OWLSConfigurations instance = null;
    private static final String propertiesFileName = "OWLSConfigurations.properties";    
    private Properties properties = new Properties();
        
    public static final String NEWLINE = System.getProperty("line.separator");
    
    
    /**
     * Constructor
     * Loads the properties file
     */
    public OWLSConfigurations() {
        loadProperties();        
    }
              
    /**
     * Singleton
     * @return the global shared instance
     */
    public static synchronized OWLSConfigurations instance() {
        if (instance == null) {
            instance = new OWLSConfigurations();
        }
        return instance;
    }
   
    
    /**************************************************************************************************************************************************************/
    
    private void logInicializationError(Exception ex) {
        Logger.getLogger(OWLSConfigurations.class.getName()).log(Level.SEVERE, "Could not iniciate logger", ex);
    }
    
    private void loadProperties() {
        try {
            properties.load(getClass().getResourceAsStream(propertiesFileName));
        } catch (Exception ex1) {
            try {
                properties.load(
                        new FileInputStream(
                        configFile(OWLSConfigurations.propertiesFileName)));
                logInicializationError(ex1);
            } catch (IOException ex2) {
                logInicializationError(ex2);
            }
        }
    }
    
    

    /**
     * @return the path of current execution directory (NX Siemens path if this plugin
     * is correctly configured and launched)
     */
    private static String currentDir() {
        String dir = System.getProperty("user.dir");
        dir = dir.substring(0, dir.lastIndexOf(File.separator));
        return dir;
    }

    /**
     * @return the path of .jar from where this class being executed
     */
    private static String currentClassDir(Class cla) {
        String dir = cla.getProtectionDomain().getCodeSource().getLocation().getPath();
        dir = dir.substring(1);
        dir = dir.substring(0, dir.lastIndexOf("/"));
        dir = dir.replaceAll("/", File.separator + File.separator);
        return dir;
    }        
    
    private static String configFile(String fileName) throws IOException   {
        return basicPath() + File.separator + "src__BSDO_OWLS" + File.separator
                + "es" + File.separator + "udc" + File.separator + "gii" + File.separator + "soc" + File.separator
                + "impl" + File.separator + "owls" + File.separator + "conf" + File.separator + fileName;
    }
    
    private static String basicPath() throws IOException {
        return ProjProperties.stringActionListPattern.matcher(new File(".").getCanonicalPath()).replaceFirst(File.separator + ProjProperties.project_basic_name);
    }
    
    
    /*****************************************************************************************************************************************************************/
    
    public boolean usePellet() {
        return "true".equals(properties.getProperty("usePellet", "false"));
    }
    
    public String dataStorePath() throws IOException {
        String dataStorePath =  properties.getProperty("dataStorePath", "data");
        if (dataStorePath.startsWith("/")) {
            return dataStorePath;
        } else {
            return basicPath() + File.separator + dataStorePath;
        }
    }        
    
}
