package org.bdigital.mob.c2020.core;

import java.io.File;
import java.util.Date;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import java.awt.Color;
import java.awt.Font;

//import org.bdigital.mob.c2020.core.OpticsAlgorithm.AppFrame;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.formats.shapefile.DBaseRecord;
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
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.ogc.kml.KMLRoot;
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import gov.nasa.worldwind.ogc.kml.io.KMLFile;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
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
		private static double miny=41.634;//41.648;
		private static double maxy=41.655;//41.650;
		private static double minx=-0.876;//-0.858291;
		private static double maxx=-0.853;//-0.85557;
				
	    public static class AppFrame extends ApplicationTemplate.AppFrame
	    {
	    	
	        public AppFrame()
	        {
	            super(true, true, true);

                
	    		try {	   
	    			
	    			Layer t82=buildLayer("/home/joana/git/traffic_sim/shapes/lines82.shp",Material.GREEN,"Tramo 82");
	    			Layer t83=buildLayer("/home/joana/git/traffic_sim/shapes/lines83.shp",Material.RED,"Tramo 83");
	    			Layer t88=buildLayer("/home/joana/git/traffic_sim/shapes/lines88.shp",Material.YELLOW,"Tramo 88");
	    			Layer t89=buildLayer("/home/joana/git/traffic_sim/shapes/lines89.shp",Material.GREEN,"Tramo 89");
	    				           
	                Layer lBing = getWwd().getModel().getLayers().getLayerByName("Bing Imagery");
	                lBing.setEnabled(true);
	                
	               /* 
	                KMLRoot kmlRoot = KMLRoot.create("/home/joana/git/traffic_sim/shapes/grid_zgz.kml");
	                kmlRoot.parse();
	                KMLController kmlController = new KMLController(kmlRoot);
	                final RenderableLayer lKML = new RenderableLayer();
	                lKML.addRenderable(kmlController);
	                lKML.setName("grid zgz");
	                insertAfterPlacenames(getWwd(), lKML);
	                */
	                
	                Layer lGrid=buildGrid("/home/joana/git/traffic_sim/shapes/grid_tweets.shp", "Tweet Density");
	                
	                insertBeforeCompass(getWwd(), lGrid);
	                insertBeforeCompass(getWwd(), t82);
	                insertBeforeCompass(getWwd(), t83);
	                insertBeforeCompass(getWwd(), t88);
	                insertBeforeCompass(getWwd(), t89);
	                	   	            
	                Layer lIncidences=createIncidenceMarkers();
	                insertBeforeCompass(getWwd(), lIncidences);
	                
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
	                                                bov.addPanToAnimator(pos, view.getHeading(), /*view.getPitch()*/a, 3000);
	                }      

	                
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
                
	        }
	        
	        	        
		    public static void main(String[] args)
		    {
				//System.setProperty("gov.nasa.worldwind.config.file", "/home/joana/worldwind/config.worldwind.properties");		
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
		    
		    protected Layer createIncidenceMarkers(){
		    	
                RenderableLayer layer = new RenderableLayer();
                
                PointPlacemark aMarker=createIncidenceMarker(41.6498,-0.85451,
                		"Road Accident");

                layer.addRenderable(aMarker);	                
                
                aMarker=createIncidenceMarker(41.64603,-0.86996,
                		"Pedestrian Accident");
                
                layer.addRenderable(aMarker);	                

                aMarker=createIncidenceMarker(41.63999,-0.85954,
                		"Road Accident");
                
                layer.addRenderable(aMarker);	                
                
                return layer;
		    	
		    }
		    
		    protected PointPlacemark createIncidenceMarker(double lat, double lon, String incidence){
		    	
                Position pointPosition = Position.fromDegrees(lat,lon);	                
                PointPlacemark pmStandard = new PointPlacemark(pointPosition);
                
                PointPlacemarkAttributes pointAttribute = new PointPlacemarkAttributes();
                pointAttribute.setImageColor(Color.red);
                pointAttribute.setLabelMaterial(Material.CYAN);
                pmStandard.setAttributes(pointAttribute);
                
                pmStandard.setValue(AVKey.DISPLAY_NAME, incidence);	                
                
                return pmStandard;
		    	
		    }
		    
		    
		    protected Layer buildGrid(String path, String name){
		    	
                RenderableLayer layer = new RenderableLayer();
                layer.setName(name);
                Shapefile shapeFile = new Shapefile(new File(path));
		    	                
                while (shapeFile.hasNext()) {
                    ShapefileRecord record = shapeFile.nextRecord();        
                    VecBuffer vectorBuffer = record.getPointBuffer(0);
                    /*
                    Double height = 500.50;
                    ExtrudedPolygon polygon = new ExtrudedPolygon();
                    polygon.setOuterBoundary(vectorBuffer.getLocations(),height);
                    
                    DBaseRecord dbrec=record.getAttributes();
                    Integer cnt= Integer.parseInt(dbrec.getValue("ptcnt").toString());
                    System.out.println(cnt);*/
                    //System.out.println(dbrec.getValue("ptcnt"));

                    ShapeAttributes normalAttributes = new BasicShapeAttributes();
                    normalAttributes.setOutlineWidth(1);
                    //normalAttributes.setDrawOutline(false);

                    Integer cnt=randInt(0,100);
                    Color c=new Color(139,37,0);
                    
                    if (cnt < 25){
                    	 c=new Color(255,69,0);
                    }else if (cnt < 50){
                    	c=new Color(238,64,0);                    	
                    }else if (cnt < 75){
                    	c=new Color(205,55,0);                    	                    	
                    }
                	normalAttributes.setInteriorMaterial(new Material(c));
                	normalAttributes.setInteriorOpacity(0.8);
                    
                    SurfacePolygon polygon = new SurfacePolygon(vectorBuffer.getLocations());
                    polygon.setAttributes(normalAttributes);
                    polygon.setValue(AVKey.DISPLAY_NAME, "num tweets: " + cnt);                    
                    
                    layer.addRenderable(polygon);
                }
                
                //This does not work	
                //layer.setOpacity(0.1);
                	
                    return layer;
		    	
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
		    
		    public static int randInt(int min, int max) {

		        // NOTE: Usually this should be a field rather than a method
		        // variable so that it is not re-seeded every call.
		        Random rand = new Random();

		        // nextInt is normally exclusive of the top value,
		        // so add 1 to make it inclusive
		        int randomNum = rand.nextInt((max - min) + 1) + min;

		        return randomNum;
		    }		    
		    
	        
	    }
}