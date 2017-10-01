package scene_engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TextureManager {
    
    private static final HashMap<String, Integer> textureMap = new HashMap<String, Integer>();
    
    private TextureManager() { }

    public static int uploadTextureToGPU(BufferedImage image) {
        int[] names = new int[1];
        GL11.glGenTextures(names);
        int name = names[0];
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        
        // Source: http://www.java-gaming.org/index.php?topic=25516.0
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); // 4 for RGBA, 3 for RGB
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));             // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));     // Alpha component. Only for RGBA
            }
        }
        buffer.flip();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        return name;
    }
    
    public static int getTextureName(String path) {
        synchronized (textureMap) {
            if (!textureMap.containsKey(path)) {
                try {
                    BufferedImage image = ImageIO.read(new File(path));
                    int name = uploadTextureToGPU(image);
                    textureMap.put(path, name);
                    return name;
                } catch (Exception e) {
                    String msg = "Error loading texture: " + path;
                    Logger.log(TextureManager.class, msg, Logger.ERROR, true);
                    Logger.logStackTrace(e);
                    Logger.assertOrCrash(false, msg);
                    return -1;
                }
            }
            return textureMap.get(path);
        }
    }
    
}
