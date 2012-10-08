package lessur.engine.lighting.old;

import java.util.ArrayList;

import lessur.engine.LessurMap;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.Tile;
import org.newdawn.slick.tiled.TileSet;


public class LightingSystem implements ILightingSystem {
	
	
    /** 
     * The values calculated for each vertex of the tile map, 
     * The 3 dimension is for each corner (topleft, topright, bottomleft, bottomright) 
     * The 4 dimension is for colour components (red, green, blue) used for colored lighting 
     */ 
	
	/*
	 * Possible optimizations
	 * So currently we are updating every tile on the map
	 * Instead we could divide the map into smalled chunks called sectors, of say 4x4 tiles
	 * When looping through all the sectors we check if it is 'dirty'. Dirty in this context means for the sector having changed
	 * We ONLY update the dirty sectors
	 */
    private float[][][][] lightValue; 
    LessurMap map = null;
    int mapTileHeight;
    int mapTileWidth;
    ArrayList<Tile> mapTiles = new ArrayList<Tile>();
    ArrayList<TileSet> mapTilesets = new ArrayList<TileSet>();
    ArrayList<Rectangle> obstructions = new ArrayList<Rectangle>();
    /** Default value to use if no light present */
	float ambientLight = 0.5f;
    
	public LightingSystem(LessurMap map,float ambientLight){
		this.map = map;
		this.mapTileWidth = map.getWidth(); 
		this.mapTileHeight = map.getHeight(); 
		//this.ambientLight = ambientLight;
		lightValue = new float[mapTileWidth + 1][mapTileHeight + 1][4][3];
		loadTilesets();
	}	
	
	public void loadTilesets(){
		for(int tilesetI = 0;tilesetI<this.map.getTileSetCount();tilesetI++){
			TileSet t = map.getTileSet(tilesetI);
			mapTilesets.add(t);
		}
	}
	
	public ArrayList<Rectangle> getObstructions(){
		return this.obstructions;
	}
	
	public ArrayList<CircleLight> getLights(){
		return this.lights;
	}
	
	public void update() {
		for (int y = 0; y < mapTileHeight ; y++) { 
            for (int x = 0; x < mapTileWidth; x++) {
                // first reset the lighting value for each corner and component (red, green, blue) 
                // ambient light here so we can see if we want even if no light 
                for (int component = 0; component < 3; component++) { 
                    lightValue[x][y][0][component] = ambientLight; // Top Left 
                    lightValue[x][y][1][component] = ambientLight; // Top Right 
                    lightValue[x][y][2][component] = ambientLight; // Bottom Left 
                    lightValue[x][y][3][component] = ambientLight; // Bottom Right 
                }
            }
		}
		
		
        for (int y = 0; y < mapTileHeight ; y++) { 
            for (int x = 0; x < mapTileWidth ; x++) { 
                
                // Value you want to check Line of Sight towards (center of target tile) 
                // Could possibly expand and make checks to each corner? 
                Vector2f loscheck = new Vector2f(x * 32 + 16, y * 32 + 16); 

                // next cycle through all the lights. Ask each light how much effect 
                // it'll have on the current vertex. 
                for (CircleLight light : lights) { 
                    // Position of current light 
                    Vector2f lightPos = new Vector2f(light.getCenterX(), light.getCenterY()); 

                    // if point to check is further away from light then it's radius, just skip check 
                    if (lightPos.distance(loscheck) >= light.getWidth()) { 
                        continue; 
                    } 

                    // Line of sight, from light poisition to target tile center 
                    Line losC = new Line(lightPos, loscheck); 
                    boolean centerLos = true; 

                    // Check objects, could use optimization, should not need to check them all? 
                    for (Rectangle r : obstructions) { 
                        // If line is broken by an object, flag that this light should not effect tile 
                        if (losC.intersects(r)) { 
                            centerLos = false; 
                        } 
                    }

                    // No line of sigh, skip 
                    if (!centerLos) { 
                        continue; 
                    } 

                    float[] effect; 

                    for (int component = 0; component < 3; component++) { 
                        // check effect on each corner 
                        
                        // top left 
                        effect = light.getEffectAtCorner(new Vector2f(x*32, y*32), colouredLights); 
                        lightValue[x][y][0][component] += effect[component]; 
                        
                        // top right 
                        effect = light.getEffectAtCorner(new Vector2f(x*32+32, y*32), colouredLights); 
                        lightValue[x][y][1][component] += effect[component]; 
                        
                        // bottom left 
                        effect = light.getEffectAtCorner(new Vector2f(x*32, y*32+32), colouredLights); 
                        lightValue[x][y][2][component] += effect[component]; 
                        
                        // bottom right 
                        effect = light.getEffectAtCorner(new Vector2f(x*32+32, y*32+32), colouredLights); 
                        lightValue[x][y][3][component] += effect[component]; 
                    } 
                } 
            } 
        } 
	}

	@Override
	public void render(Graphics g) {
				
		for (int y = 0; y < mapTileHeight ; y++) { 
            for (int x = 0; x < mapTileWidth; x++) { 
                // get the appropriate image to draw for the current tile 
            	Image image = null;
            	for(int l = map.getLayerCount()-1;l > -1;l--){
            		if(image == null){
            			image = map.getTileImage(x, y, l);
            			continue;
            		}
            	}
            	
            	
                    // if lighting is on apply the lighting values we've 
                    // calculated for each vertex to the image. We can apply 
                    // colour components here as well as just a single value. 
                    image = light(x, y, image);
                

                // draw the image with it's newly declared vertex colours 
                // to the display 
                image.draw(x * 32, y * 32, 32, 32); 
            } 
        } 
        
        // Draw an image on each object, easy here since we only use one imagefile 
        for (Rectangle r : obstructions) { 
           boolean canRenderObject = true; 
            Image object = map.getTileImage((int)r.getX()/32, (int)r.getY()/32, 1); 
            if(object == null){ 
               canRenderObject = false; 
               System.out.println("Cant render object at "+r.getX()/32+","+r.getY()/32);
               g.drawString("/:/", r.getX(), r.getY());
            } 
            if(canRenderObject){ 
                // Divide position by tilesize since blend() checks per maptile X,Y cord 
                object = light((int) r.getX() / 32, (int) r.getY() / 32, object); 
            
            
            // finally draw it 
            object.draw(r.getX(), r.getY(), 32, 32); 
            } 
        }
	}
	
	@Override
	public Image light(int x, int y, Image i) {
		float[][] val = lightValue[x][y];
		boolean skipTopLeft = false, skipTopRight = false, skipBottomLeft = false, skipBottomRight = false;
		if(y == 0){
			i.setColor(Image.TOP_LEFT, ambientLight, ambientLight, ambientLight, 1); 
			i.setColor(Image.TOP_RIGHT, ambientLight, ambientLight, ambientLight, 1); 
			skipTopLeft = true;
			skipTopRight = true;
		}
		if(x == 0){
			i.setColor(Image.TOP_LEFT, ambientLight, ambientLight, ambientLight, 1); 
			i.setColor(Image.BOTTOM_LEFT, ambientLight, ambientLight, ambientLight, 1);
			skipTopLeft = true;
			skipBottomLeft = true;
		}
		if(x == (map.getWidth()-1)){
			i.setColor(Image.BOTTOM_RIGHT, ambientLight, ambientLight, ambientLight, 1); 
			i.setColor(Image.TOP_RIGHT, ambientLight, ambientLight, ambientLight, 1);
			skipBottomRight = true;
			skipTopRight = true;
		}
		if(y == (map.getHeight()-1)){
			i.setColor(Image.BOTTOM_LEFT, ambientLight, ambientLight, ambientLight, 1); 
			i.setColor(Image.BOTTOM_RIGHT, ambientLight, ambientLight, ambientLight, 1); 
			skipBottomLeft = true;
			skipBottomRight = true;
		}
		
		if(!skipTopLeft){
        float topLeftRed = (lightValue[x - 1][y - 1][3][0] 
                + lightValue[x][y - 1][2][0] 
                + lightValue[x - 1][y][1][0] 
                + lightValue[x - 1][y][3][0] 
                + lightValue[x][y - 1][3][0] 
                + val[0][0] + val[1][0] + val[2][0] + val[3][0]) / 9; 

        float topLeftGreen = (lightValue[x - 1][y - 1][3][1] 
                + lightValue[x][y - 1][2][1] 
                + lightValue[x - 1][y][1][1] 
                + lightValue[x - 1][y][3][1] 
                + lightValue[x][y - 1][3][1] 
                + val[0][1] + val[1][1] + val[2][1] + val[3][1]) / 9; 

        float topLeftBlue = (lightValue[x - 1][y - 1][3][2] 
                + lightValue[x][y - 1][2][2] 
                + lightValue[x - 1][y][1][2] 
                + lightValue[x - 1][y][3][2] 
                + lightValue[x][y - 1][3][2] 
                + val[0][2] + val[1][2] + val[2][2] + val[3][2]) / 9; 
        i.setColor(Image.TOP_LEFT, topLeftRed * 0.8f, topLeftGreen * 0.8f, topLeftBlue * 0.8f, 1); 
		}


        // ------------------- 
		if(!skipTopRight){
        float topRightRed = (lightValue[x][y - 1][2][0] 
                + lightValue[x][y - 1][3][0] 
                + lightValue[x + 1][y - 1][2][0] 
                + lightValue[x + 1][y][0][0] 
                + lightValue[x + 1][y][2][0] 
                + val[0][0] + val[1][0] + val[2][0] + val[3][0]) / 9; 

        float topRightGreen = (lightValue[x][y - 1][2][1] 
                + lightValue[x][y - 1][3][1] 
                + lightValue[x + 1][y - 1][2][1] 
                + lightValue[x + 1][y][0][1] 
                + lightValue[x + 1][y][2][1] 
                + val[0][1] + val[1][1] + val[2][1] + val[3][1]) / 9; 

        float topRightBlue = (lightValue[x][y - 1][2][2] 
                + lightValue[x][y - 1][3][2] 
                + lightValue[x + 1][y - 1][2][2] 
                + lightValue[x + 1][y][0][2] 
                + lightValue[x + 1][y][2][2] 
                + val[0][2] + val[1][2] + val[2][2] + val[3][2]) / 9; 
        i.setColor(Image.TOP_RIGHT, topRightRed * 0.8f, topRightGreen * 0.8f, topRightBlue * 0.8f, 1); 
		}
		
        // ------------------- 
		if(!skipBottomLeft){
        float bottomLeftRed = (lightValue[x - 1][y][3][0] 
                + lightValue[x - 1][y][1][0] 
                + lightValue[x - 1][y + 1][1][0] 
                + lightValue[x][y + 1][0][0] 
                + lightValue[x][y + 1][1][0] 
                + val[0][0] + val[1][0] + val[2][0] + val[3][0]) / 9; 

        float bottomLeftGreen = (lightValue[x - 1][y][3][1] 
                + lightValue[x - 1][y][1][1] 
                + lightValue[x - 1][y + 1][1][1] 
                + lightValue[x][y + 1][0][1] 
                + lightValue[x][y + 1][1][1] 
                + val[0][1] + val[1][1] + val[2][1] + val[3][1]) / 9; 

        float bottomLeftBlue = (lightValue[x - 1][y][3][2] 
                + lightValue[x - 1][y][1][2] 
                + lightValue[x - 1][y + 1][1][2] 
                + lightValue[x][y + 1][0][2] 
                + lightValue[x][y + 1][1][2] 
                + val[0][2] + val[1][2] + val[2][2] + val[3][2]) / 9; 
        i.setColor(Image.BOTTOM_LEFT, bottomLeftRed * 0.8f, bottomLeftGreen * 0.8f, bottomLeftBlue * 0.8f, 1); 
		}
		
        // ------------------- 
		if(!skipBottomRight){
        float bottomRightRed = (lightValue[x][y + 1][0][0] 
                + lightValue[x][y + 1][1][0] 
                + lightValue[x + 1][y][0][0] 
                + lightValue[x + 1][y][2][0] 
                + lightValue[x + 1][y + 1][0][0] 
                + val[0][0] + val[1][0] + val[2][0] + val[3][0]) / 9; 

        float bottomRightGreen = (lightValue[x][y + 1][0][1] 
                + lightValue[x][y + 1][1][1] 
                + lightValue[x + 1][y][0][1] 
                + lightValue[x + 1][y][2][1] 
                + lightValue[x + 1][y + 1][0][1] 
                + val[0][1] + val[1][1] + val[2][1] + val[3][1]) / 9; 

        float bottomRightBlue = (lightValue[x][y + 1][0][2] 
                + lightValue[x][y + 1][1][2] 
                + lightValue[x + 1][y][0][2] 
                + lightValue[x + 1][y][2][2] 
                + lightValue[x + 1][y + 1][0][2] 
                + val[0][2] + val[1][2] + val[2][2] + val[3][2]) / 9; 
        i.setColor(Image.BOTTOM_RIGHT, bottomRightRed * 0.8f, bottomRightGreen * 0.8f, bottomRightBlue * 0.8f, 1);
		}
        return i;
	} 
	
}
