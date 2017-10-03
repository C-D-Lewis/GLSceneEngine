package scene_engine;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

public class TileSheet {

    private BufferedImage sprites[][];
    private HashMap<Point, Integer> textureMap = new HashMap<Point, Integer>();
    private int tileSize;

    public TileSheet(String path, int tileSize, boolean discardBlank) {
        this.tileSize = tileSize;

        try {
            BufferedImage sheet = ImageIO.read(new File(path));
            Logger.log(TileSheet.class, "Path is " + path, Logger.INFO, true);
            
            boolean b = sheet.getType() == BufferedImage.TYPE_4BYTE_ABGR;
            Logger.assertOrCrash(b, "Sprite sheet should be BufferedImage.TYPE_4BYTE_ABGR");
            
            int gridWidth = sheet.getWidth() / tileSize;
            int gridHeight = sheet.getHeight() / tileSize;
            Logger.log(TileSheet.class, "Tile sheet of " + sheet.getWidth() + "x" + sheet.getHeight() + " is " + gridWidth + "x" + gridHeight + " tiles.", Logger.INFO, true);
            sprites = new BufferedImage[gridWidth][gridHeight];

            int count = 0;
            for(int y = 0; y < gridHeight; y++) {
                for(int x = 0; x < gridWidth; x++) {
                    sprites[x][y] = sheet.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize);
                    if(discardBlank) {
                        if(checkSpriteBlank(sprites[x][y])) sprites[x][y] = null;    //Throw it away
                    } else {
                        textureMap.put(new Point(x, y), TextureManager.uploadTextureToGPU(sprites[x][y]));    // Slightly genius!
                        count++;
                    }
                }
            }
            Logger.log(TileSheet.class, "Found " + count + " tiles.", Logger.INFO, true);
        } catch (IOException e) {
            Logger.logStackTrace(e);
            Logger.assertOrCrash(false, "Path is invalid: "  + path);
        }
    }
    
    // Check to see if a tile is completely blank
    private boolean checkSpriteBlank(BufferedImage inSprite) {
        for(int y = 0; y < tileSize; y++) {
            for(int x = 0; x < tileSize; x++) {
                if(inSprite.getRGB(x, y) >> 24 != 0x00) return false;  // TYPE_4BYTE_ABGR
            }
        }
        return true;
    }
    
    public BufferedImage getSprite(int x, int y) { return sprites[x][y]; }
    
    public int bindTileTextureToGPU(Point tilePoint) {
        if(textureMap.get(tilePoint) == null) {
            Logger.log(TileSheet.class, "Null texture: " + tilePoint.toString(), Logger.ERROR, false);
            return 0;
        }

        int name = textureMap.get(tilePoint);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
        return name;
    }
    
}
