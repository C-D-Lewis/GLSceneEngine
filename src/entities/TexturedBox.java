package entities;

import core.BuildConfig;
import scene_engine.Engine;
import scene_engine.GLHelpers;
import scene_engine.Loopable;
import scene_engine.TextureManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TexturedBox implements Loopable {

    private static final int SPEED = 5;

    private BufferedImage texture;
    private Rectangle bounds;
    private String texPath;

    private int dx = SPEED, dy = SPEED;

    public TexturedBox(Rectangle bounds, String texPath) {
        this.bounds = new Rectangle(bounds);
        this.texPath = texPath;

        if(Engine.getRenderMode().equals(Engine.RenderMode.JAVA_2D)) {
            try { texture = ImageIO.read(new File(texPath)); }
            catch (IOException e) { e.printStackTrace(); }
        }
    }

    @Override
    public void update() {
        bounds.x += dx;
        bounds.y += dy;

        if(bounds.x + bounds.width > BuildConfig.SCREEN_RECT.width) { dx = -SPEED; }
        if(bounds.x < 0) { dx = SPEED; }
        if(bounds.y + bounds.height > BuildConfig.SCREEN_RECT.height) { dy = -SPEED; }
        if(bounds.y < 0) { dy = SPEED; }
    }

    @Override
    public void draw() {
        GLHelpers.pushNewColor(Color.RED);
        int name = TextureManager.getTextureName(texPath);
        GLHelpers.drawImageFromTextureName(name, bounds.x, bounds.y, bounds.width, bounds.height);
        GLHelpers.popPreviousColor();
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.drawImage(texture, null, bounds.x, bounds.y);
    }
}
