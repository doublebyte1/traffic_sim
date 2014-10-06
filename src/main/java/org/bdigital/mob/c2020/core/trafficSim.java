package org.bdigital.mob.c2020.core;

import java.io.File;

//import org.bdigital.mob.c2020.core.OpticsAlgorithm.AppFrame;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecordPolyline;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolylines;
import gov.nasa.worldwind.util.VecBuffer;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.util.RandomShapeAttributes;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;

/**
 * Traffic Simulator
 * 
 * @author Joana Simoes (jsimoes@bdigital.org)
 * @version 0.1 06/11/2014
 */
public class trafficSim extends ApplicationTemplate{
		private static double miny=41.648;
		private static double maxy=41.650;
		private static double minx=-0.858291;
		private static double maxx=-0.85557;
		
	    public static class AppFrame extends ApplicationTemplate.AppFrame
	    {
	    	
	        public AppFrame()
	        {
	            super(true, true, true);

                
	    		try {	   
	    			
	    			Layer t82=buildLayer("/home/joana/git/traffic_sim/shapes/lines82.shp",Material.GREEN,"Tramo 82");
	    			Layer t83=buildLayer("/home/joana/git/traffic_sim/shapes/lines83.shp",Material.RED,"Tramo 83");
	    				           
	                Layer lBing = getWwd().getModel().getLayers().getLayerByName("Bing Imagery");
	                lBing.setEnabled(true);
	                
	                insertBeforeCompass(getWwd(), t82);
	                insertBeforeCompass(getWwd(), t83);
	                
	                // Update layer panel
	                this.getLayerPanel().update(this.getWwd());
	    		    
	    			LatLon p1=new LatLon(Position.fromDegrees(miny,minx,0));
	    			LatLon p2=new LatLon(Position.fromDegrees(maxy,maxx,0));		
	    	
	    			Sector boundingSector = Sector.boundingSector(p1,p2);
	    					
	    			LatLon centroid=boundingSector.getCentroid();
	    			Position pos=new Position(centroid.getLatitude(),
	    					centroid.getLongitude(),0);
	    			
	    			
	                View view = this.getWwd().getView();
	                //Globe globe = this.getWwd().getModel().getGlobe();
	                Angle a=Angle.fromDegrees(45.0);
	                if(view instanceof BasicOrbitView) {
	                        BasicOrbitView bov = (BasicOrbitView)view;
	                                                bov.stopAnimations();
	                                                bov.addPanToAnimator(pos, view.getHeading(), /*view.getPitch()*/a, 2000);
	                }                
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
                
	        }
	        
	        	        
		    public static void main(String[] args)
		    {
		        ApplicationTemplate.start("Traffic Forecast on NASA World Wind", AppFrame.class);
		    }
		    
		    protected Renderable createPolyline(ShapefileRecord record, ShapeAttributes attrs)
		    {
			    SurfacePolylines shape = new SurfacePolylines(
			    Sector.fromDegrees(((ShapefileRecordPolyline) record).getBoundingRectangle()),
			    record.getCompoundPointBuffer());
			    shape.setAttributes(attrs);
			    return shape;
		    }
		    
		    protected Layer buildLayer(String path, Material m, String name){
		    	
                RenderableLayer layer = new RenderableLayer();
                layer.setName(name);
                //Creating a shapefile based on an ordinary File object
                Shapefile shapeFile = new Shapefile(new File(path));
                //Setting attributes for the loaded shapefile
                ShapeAttributes normalAttributes = new BasicShapeAttributes();
                normalAttributes.setOutlineMaterial(m);
                normalAttributes.setOutlineWidth(100);	    			
    			
                while (shapeFile.hasNext())
                {
                    ShapefileRecord record = shapeFile.nextRecord();
                
                    SurfacePolylines shape = new SurfacePolylines(
                            Sector.fromDegrees(((ShapefileRecordPolyline) record).getBoundingRectangle()),
                            record.getCompoundPointBuffer());
                        shape.setAttributes(normalAttributes);	                        
                    layer.addRenderable(shape);
                }	                
                
                //Closing shape file
                shapeFile.close();
		    	
                return layer;
		    	
		    }
	        
	    }
}