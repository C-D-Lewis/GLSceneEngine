package scene_engine;

import java.awt.*;

public interface Loopable {
    public void update();
    public void draw();
    public void draw(Graphics2D g2d);
}
