package scene_engine;

import java.awt.Point;

public class Helpers {
    
    public static Point i2xy(int i, int rowSize) { return new Point(i % rowSize, i / rowSize); }

}
