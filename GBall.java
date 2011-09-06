/**
 * Creates a filled brick to be used in Breakout
 * @author kenziemac244
 *
 */

import java.awt.*;
import acm.graphics.*;

public class GBall extends GOval {
	
	public GBall(int width, int height, Color color) {
		super(width, height);
		setFilled(true);
		setFillColor(color);
	}

}
