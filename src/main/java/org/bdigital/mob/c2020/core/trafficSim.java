package org.bdigital.mob.c2020.core;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

/**
 * Traffic Simulator
 * 
 * @author Joana Simoes (jsimoes@bdigital.org)
 * @version 0.1 06/11/2014
 */
public class trafficSim extends ApplicationTemplate{
	
	
	    public static class AppFrame extends ApplicationTemplate.AppFrame
	    {
	    	
	        public AppFrame()
	        {
	            super(true, true, true);
                View view = this.getWwd().getView();
	        }
	    }
}