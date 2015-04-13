package org.bdigital.mob.c2020.core;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.FieldPosition;

//import org.bdigital.mob.c2020.core.OpticsAlgorithm.AppFrame;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.data.BufferWrapperRaster;
import gov.nasa.worldwind.data.BufferedImageRaster;
import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.data.DataRasterReader;
import gov.nasa.worldwind.data.DataRasterReaderFactory;
import gov.nasa.worldwind.formats.shapefile.DBaseRecord;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecordPolyline;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.ogc.kml.KMLRoot;
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import gov.nasa.worldwind.ogc.kml.io.KMLFile;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfacePolylines;
import gov.nasa.worldwind.util.BufferFactory;
import gov.nasa.worldwind.util.BufferWrapper;
import gov.nasa.worldwind.util.VecBuffer;
import gov.nasa.worldwind.util.WWBufferUtil;
import gov.nasa.worldwind.util.WWMath;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurface;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurfaceAttributes;
import gov.nasa.worldwindx.examples.analytics.AnalyticSurfaceLegend;
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
		protected static final double HUE_BLUE = 240d / 360d;
		protected static final double HUE_RED = 0d / 360d;		
		protected static final int DEFAULT_RANDOM_ITERATIONS = 100;
		protected static final double DEFAULT_RANDOM_SMOOTHING = 0.0d;
		
	    public static class AppFrame extends ApplicationTemplate.AppFrame
	    {
	    	
	        private static JTextField textField;
	    	
	        public AppFrame()
	        {
	            super(true, true, true);

	            
	            removeBaseLayers();
	            
				JLabel labelTextField = new JLabel("Weather Conditions: dry, good visibility, strong winds;");
				// Add controls
				
				GridLayout experimentLayout = new GridLayout(0,2);
				textField = new JTextField();
				JPanel fieldPanel = new JPanel(experimentLayout);
				
				fieldPanel.add(labelTextField);
				
				JPanel MainPanel = new JPanel();
				MainPanel.add(fieldPanel, BorderLayout.NORTH);
				
				this.getContentPane().add(MainPanel, BorderLayout.NORTH);			        
				
	    		try {	   
	    			
	    			Layer t82=buildLayer("/home/joana/git/traffic_sim/shapes/lines82.shp",Material.GREEN,"Tramo 82");
	    			Layer t83=buildLayer("/home/joana/git/traffic_sim/shapes/lines83.shp",Material.RED,"Tramo 83");
	    			Layer t88=buildLayer("/home/joana/git/traffic_sim/shapes/lines88.shp",Material.YELLOW,"Tramo 88");
	    			Layer t89=buildLayer("/home/joana/git/traffic_sim/shapes/lines89.shp",Material.BLACK,"Tramo 89");
	    				           
	                Layer lBing = getWwd().getModel().getLayers().getLayerByName("Bing Imagery");
	                lBing.setEnabled(true);
	                
	                //Layer lGrid=buildGrid("/home/joana/git/traffic_sim/shapes/grid_tweets.shp", "Actividad en las Redes Sociales");
	                //insertBeforeCompass(getWwd(), lGrid);
	                
	                Layer analyticSurfaceLayer=createRandomAltitudeSurface(HUE_BLUE, HUE_RED, 50, 50);
	                
	                insertBeforeCompass(getWwd(), analyticSurfaceLayer);
	                
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
	    					centroid.getLongitude(),1000);
	    			
	    			
	                View view = this.getWwd().getView();
	                //Globe globe = this.getWwd().getModel().getGlobe();
	                Angle a=Angle.fromDegrees(40.0);
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
		        ApplicationTemplate.start("Predicción de Estado de Tráfico en Zaragoza", AppFrame.class);
		    }
		    
		    protected void removeBaseLayers()
		    {
		    	//layers we want to remove!
				String[] layers = {"USDA NAIP","USDA NAIP USGS","USGS Topographic Maps 1:250K",
						"USGS Topographic Maps 1:100K","USGS Topographic Maps 1:24K",
						"USGS Urban Area Ortho"};
						
				
				
		    	try{
/*		    		
		    		LayerList layers=getWwd().getModel().getLayers();
		    		for (int i=0; i < layers.size(); i++){
		    			System.out.println(layers.get(i).getName());
		    		}
	*/	    	
		    		for (int i=0; i < layers.length; i++){
			            Layer l = getWwd().getModel().getLayers().getLayerByName(layers[i]);
			            getWwd().getModel().getLayers().remove(l);	            		    			
		    		}
	    		
		            
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}

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
                layer.setName("Incidencias en la Vía Pública");
                
                PointPlacemark aMarker=createIncidenceMarker(41.6498,-0.85451,
                		"Corte Tráfico");

                layer.addRenderable(aMarker);	                
                
                aMarker=createIncidenceMarker(41.64603,-0.86996,
                		"Corte Tráfico");
                
                layer.addRenderable(aMarker);	                

                aMarker=createIncidenceMarker(41.63999,-0.85954,
                		"Corte de agua");
                
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
                    DBaseRecord dbrec=record.getAttributes();
                    Integer cnt= Integer.parseInt(dbrec.getValue("ptcnt").toString());
                    System.out.println(cnt);*/
                    //System.out.println(dbrec.getValue("ptcnt"));

                    ShapeAttributes normalAttributes = new BasicShapeAttributes();
                    normalAttributes.setOutlineWidth(1);
                    //normalAttributes.setDrawOutline(false);

                    Integer cnt=randInt(0,100);
                    Color c=new Color(228,48,48);
                    
                    /*
                    Color c=new Color(139,37,0);
                    
                    if (cnt < 25){
                    	 c=new Color(255,69,0);
                    }else if (cnt < 50){
                    	c=new Color(238,64,0);                    	
                    }else if (cnt < 75){
                    	c=new Color(205,55,0);                    	                    	
                    }*/
                	normalAttributes.setInteriorMaterial(new Material(c));
                	normalAttributes.setOutlineMaterial(new Material(c));
                	
                	//normalAttributes.setInteriorOpacity(0.8);
                    
                    //SurfacePolygon polygon = new SurfacePolygon(vectorBuffer.getLocations());
                    
                    ExtrudedPolygon polygon = new ExtrudedPolygon();
                    polygon.setOuterBoundary(vectorBuffer.getLocations(),100*Double.parseDouble(Integer.toString(cnt)));

                    
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
	


			protected static Layer createRandomAltitudeSurface(double minHue, double maxHue, int width, int height)					 
					 {
				
		                RenderableLayer outLayer = new RenderableLayer();
		                //outLayer.setOpacity(0.1);
		                outLayer.setEnabled(false);
		                outLayer.setPickEnabled(false);
		                outLayer.setName("Actividad en las Redes Sociales");

						double minValue = 1;//-200e3;
						double maxValue = 1000;//200e3;
						AnalyticSurface surface = new AnalyticSurface();
						 
		    			LatLon p1=new LatLon(Position.fromDegrees(miny,minx,0));
		    			LatLon p2=new LatLon(Position.fromDegrees(maxy,maxx,0));		
		    	
		    			Sector boundingSector = Sector.boundingSector(p1,p2);
			    				    					    			
						 surface.setSector(boundingSector);
						 surface.setAltitude(200/*400e3*/);
						 surface.setDimensions(width, height);
						 surface.setClientLayer(outLayer);
						 outLayer.addRenderable(surface);
						 BufferWrapper firstBuffer = randomGridValues(width, height, minValue, maxValue);
						 BufferWrapper secondBuffer = randomGridValues(width, height, minValue * 2d, maxValue / 2d);
		                 
						 surface.setValues(createMixedColorGradientGridValues(
						 11.0, firstBuffer, secondBuffer, minValue, maxValue, minHue, maxHue));

						 AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
						 attr.setShadowOpacity(0);
						 
						 surface.setSurfaceAttributes(attr);
						 final double altitude = surface.getAltitude();
						 final double verticalScale = surface.getVerticalScale();
						 Format legendLabelFormat = new DecimalFormat("# Tweets"/*"# km"*/)
						 {
							 public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition)
							 {
							 double altitudeMeters = altitude + verticalScale * number;
							 double altitudeKm = altitudeMeters * WWMath.METERS_TO_KILOMETERS;
							 return super.format(number, result, fieldPosition);
							 }
						 };
						 AnalyticSurfaceLegend legend = AnalyticSurfaceLegend.fromColorGradient(minValue, maxValue, minHue, maxHue,
						 AnalyticSurfaceLegend.createDefaultColorGradientLabels(minValue, maxValue, legendLabelFormat),
						 AnalyticSurfaceLegend.createDefaultTitle("Twiter Activity"));
						 legend.setOpacity(1);
						 legend.setScreenLocation(new Point(650, 300));
						 //legend.setClientLayer(outLayer);
						 outLayer.addRenderable(createLegendRenderable(surface, 300, legend));
						 return outLayer;
					 }		    
		    
			 protected static void mixValuesOverTime(
					 final long timeToMix,
					 final BufferWrapper firstBuffer, final BufferWrapper secondBuffer,
					 final double minValue, final double maxValue, final double minHue, final double maxHue,
					 final AnalyticSurface surface)
					 {
						 Timer timer = new Timer(20, new ActionListener()
						 {
						 private long startTime = -1;
						 public void actionPerformed(ActionEvent e)
						 {
						 if (this.startTime < 0)
						 this.startTime = System.currentTimeMillis();
						 double t = (double) (e.getWhen() - this.startTime) / (double) timeToMix;
						 int ti = (int) Math.floor(t);
						 double a = t - ti;
						 if ((ti % 2) == 0)
						 a = 1d - a;
						 surface.setValues(createMixedColorGradientGridValues(
						 a, firstBuffer, secondBuffer, minValue, maxValue, minHue, maxHue));
						 
						 //System.out.println(surface.getClientLayer().getName());
						 
						 if (surface.getClientLayer() != null)
							 surface.getClientLayer().firePropertyChange(AVKey.LAYER, null, surface.getClientLayer());
						 }
						 });
						 timer.start();
					 }
			 
			 public static Iterable<? extends AnalyticSurface.GridPointAttributes> createMixedColorGradientGridValues(double a,
					 BufferWrapper firstBuffer, BufferWrapper secondBuffer, double minValue, double maxValue,
					 double minHue, double maxHue)
					 {
						 ArrayList<AnalyticSurface.GridPointAttributes> attributesList
						 = new ArrayList<AnalyticSurface.GridPointAttributes>();
				 
				 		try{
						 long length = Math.min(firstBuffer.length(), secondBuffer.length());
						 for (int i = 0; i < length; i++)
						 {
							 double value = WWMath.mixSmooth(a, firstBuffer.getDouble(i), secondBuffer.getDouble(i));
							 attributesList.add(
							 AnalyticSurface.createColorGradientAttributes(value, minValue, maxValue, minHue, maxHue));
						 }
			    		} catch (Exception e) {
			    			e.printStackTrace();
			    		}
					 return attributesList;
					 }		
			 
			 protected static Renderable createLegendRenderable(final AnalyticSurface surface, final double surfaceMinScreenSize,
					 final AnalyticSurfaceLegend legend)
					 {
					 	return new Renderable()
					 {
					 public void render(DrawContext dc)
					 {
						 Extent extent = surface.getExtent(dc);
						 if (!extent.intersects(dc.getView().getFrustumInModelCoordinates()))
						 return;
						 if (WWMath.computeSizeInWindowCoordinates(dc, extent) < surfaceMinScreenSize)
						 return;
						 legend.render(dc);
						 }
						 };
					 }			 
			 
			 
			 public static BufferWrapper randomGridValues(int width, int height, double min, double max)
			 {
				 return randomGridValues(width, height, min, max, DEFAULT_RANDOM_ITERATIONS, DEFAULT_RANDOM_SMOOTHING,
				 new BufferFactory.DoubleBufferFactory());
			 }			 
			 
			 public static BufferWrapper randomGridValues(int width, int height, double min, double max, int numIterations,
					 double smoothness, BufferFactory factory)
					 {
						 int numValues = width * height;
						 double[] values = new double[numValues];
						 for (int i = 0; i < numIterations; i++)
						 {
							 double offset = 1d - (i / (double) numIterations);
							 int x1 = (int) Math.round(Math.random() * (width - 1));
							 int x2 = (int) Math.round(Math.random() * (width - 1));
							 int y1 = (int) Math.round(Math.random() * (height - 1));
							 int y2 = (int) Math.round(Math.random() * (height - 1));
							 int dx1 = x2 - x1;
							 int dy1 = y2 - y1;
							 for (int y = 0; y < height; y++)
							 {
								 int dy2 = y - y1;
								 for (int x = 0; x < width; x++)
								 {
									 int dx2 = x - x1;
									 if ((dx2 * dy1 - dx1 * dy2) >= 0)
									 values[x + y * width] += offset;
								 }
							 }
						 }
						 smoothValues(width, height, values, smoothness);
						 scaleValues(values, numValues, min, max);
						 BufferWrapper buffer = factory.newBuffer(numValues);
						 buffer.putDouble(0, values, 0, numValues);
						 return buffer;
					 }			 
			 
			 
			 protected static void scaleValues(double[] values, int count, double minValue, double maxValue)
			 {
				 double min = Double.MAX_VALUE;
				 double max = -Double.MAX_VALUE;
				 for (int i = 0; i < count; i++)
				 {
					 if (min > values[i])
					 min = values[i];
					 if (max < values[i])
					 max = values[i];
				 }
				 for (int i = 0; i < count; i++)
				 {
					 values[i] = (values[i] - min) / (max - min);
					 values[i] = minValue + values[i] * (maxValue - minValue);
				 }
			 }
			 protected static void smoothValues(int width, int height, double[] values, double smoothness)
			 {
				 // top to bottom
				 for (int x = 0; x < width; x++)
				 {
					 smoothBand(values, x, width, height, smoothness);
				 }
				 // bottom to top
				 int lastRowOffset = (height - 1) * width;
				 for (int x = 0; x < width; x++)
				 {
					 smoothBand(values, x + lastRowOffset, -width, height, smoothness);
				 }
				 // left to right
				 for (int y = 0; y < height; y++)
				 {
					 smoothBand(values, y * width, 1, width, smoothness);
				 }
				 // right to left
				 int lastColOffset = width - 1;
				 for (int y = 0; y < height; y++)
				 {
					 smoothBand(values, lastColOffset + y * width, -1, width, smoothness);
				 }
			 }
			 protected static void smoothBand(double[] values, int start, int stride, int count, double smoothness)
			 {
				 double prevValue = values[start];
				 int j = start + stride;
				 for (int i = 0; i < count - 1; i++)
				 {
					 values[j] = smoothness * prevValue + (1 - smoothness) * values[j];
					 prevValue = values[j];
					 j += stride;
				 }
			 }
			 
			 
			 
	        
	    }
}