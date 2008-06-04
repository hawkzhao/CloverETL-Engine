/*
*    jETeL/Clover - Java based ETL application framework.
*    Copyright (C) 2005-06  Javlin Consulting <info@javlinconsulting.cz>
*    
*    This library is free software; you can redistribute it and/or
*    modify it under the terms of the GNU Lesser General Public
*    License as published by the Free Software Foundation; either
*    version 2.1 of the License, or (at your option) any later version.
*    
*    This library is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU    
*    Lesser General Public License for more details.
*    
*    You should have received a copy of the GNU Lesser General Public
*    License along with this library; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
*/
package org.jetel.util.primitive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.jetel.graph.TransformationGraph;
import org.jetel.util.property.PropertyRefResolver;

/**
 * This class provides typed access to attributes in java.util.Properties.
 * Extends java.util.Properties and implements variations of getProperty() method
 * for accessing boolean, int, long, double and of course String values.
 * 
 * @author Martin Zatopek (martin.zatopek@javlinconsulting.cz)
 *         (c) Javlin Consulting (www.javlinconsulting.cz)
 *
 * @created 17.10.2006
 */
public class TypedProperties extends Properties {

	private static final long serialVersionUID = -3251111555515215464L;
	
	private PropertyRefResolver propertyRefResolver;
	
    public TypedProperties() {
        super();
    }
    
    /**
     * Behaviour of this contructor doesn't follow its parant java.util.Properties.
     * Properties given as parameter are copied not seted as default values.
     * @param properties
     */
    public TypedProperties(Properties properties) {
    	this(properties, (Properties) null);
    }

    /**
     * @param properties
     * @param graph graph is used to property reference resolving
     */
    public TypedProperties(Properties properties, TransformationGraph graph) {
    	this(properties, graph != null ? graph.getGraphProperties() : null);
    }

    /**
     * @param properties
     * @param refProperties properties to reference resolving
     */
    public TypedProperties(Properties properties, Properties refProperties) {
        super();
        
        //preset given properties
        if(properties != null) {
            putAll(properties);
        }
        
        //initialize propertyRefResolver
        if(refProperties != null) {
        	propertyRefResolver = new PropertyRefResolver(properties);
        }
    }

    /**
     * Doesn't overwrite existing properties.
     * @see java.util.Properties#load(java.io.InputStream)
     */
    public synchronized void loadSafe(InputStream inStream) throws IOException {
        Properties tempProperties = new Properties();
        tempProperties.load(inStream);
        
        for(Enumeration e = tempProperties.propertyNames(); e.hasMoreElements();) {
            String propertyName = (String) e.nextElement();
            setPropertySafe(propertyName, tempProperties.getProperty(propertyName));
        }
    }
    
    /**
     * Doesn't overwrite existing properties.
     * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
     */
    public synchronized Object setPropertySafe(String key, String value) {
        if(getProperty(key) == null) {
            return super.setProperty(key, value);
        } else {
            return getProperty(key);
        }
    }
    
    public Boolean getBooleanProperty(String key) {
        String prop = getProperty(key);
        prop = resolvePropertyReferences(prop);
        return (prop != null) ? Boolean.valueOf(prop) : null;
    }
    
    public Boolean getBooleanProperty(String key, boolean defaultValue) {
        Boolean prop = getBooleanProperty(key);
        return (prop != null) ? prop : defaultValue;
    }

    public Integer getIntProperty(String key) {
        String prop = getProperty(key);
        prop = resolvePropertyReferences(prop);
        return (prop != null) ? Integer.valueOf(prop) : null;
    }
    
    public Integer getIntProperty(String key, int defaultValue) {
        Integer prop = getIntProperty(key);
        return (prop != null) ? prop : defaultValue;
    }
    
    public Long getLongProperty(String key) {
        String prop = getProperty(key);
        prop = resolvePropertyReferences(prop);
        return (prop != null) ? Long.valueOf(prop) : null;
    }
    
    public Long getLongProperty(String key, long defaultValue) {
        Long prop = getLongProperty(key);
        return (prop != null) ? prop : defaultValue;
    }

    public Double getDoubleProperty(String key) {
        String prop = getProperty(key);
        prop = resolvePropertyReferences(prop);
        return (prop != null) ? Double.valueOf(prop) : null;
    }
    
    public Double getDoubleProperty(String key, double defaultValue) {
        Double prop = getDoubleProperty(key);
        return (prop != null) ? prop : defaultValue;
    }

    public String getStringProperty(String key) {
        String prop = getProperty(key);
        prop = resolvePropertyReferences(prop);
        return prop;
    }
    
    public String getStringProperty(String key, String defaultValue) {
        String prop = getStringProperty(key);
        return (prop != null) ? prop : defaultValue;
    }

    /**
     * Returns subset of properties, which names start with given prefix.
     * @param prefix
     * @return
     */
    public TypedProperties getPropertiesStartWith(String prefix) {
    	Properties ret = new Properties();

    	for(Enumeration e = propertyNames(); e.hasMoreElements();) {
            String propertyName = (String) e.nextElement();
            if(propertyName.startsWith(prefix)) {
            	ret.setProperty(propertyName, getProperty(propertyName));
            }
        }
        
        return new TypedProperties(ret, propertyRefResolver != null ? propertyRefResolver.getProperties() : null);
    }
    
    private String resolvePropertyReferences(String s) {
    	if(propertyRefResolver != null && s != null) {
    		return propertyRefResolver.resolveRef(s);
    	} else {
    		return s;
    	}
    }
    
}
