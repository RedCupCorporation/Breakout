/**
 * Creates a filled brick to be used in Breakout
 * @author kenziemac244
 *
 */

import java.awt.*;
import acm.graphics.*;

public class GBrick extends GRect {
	
	public GBrick(int width, int height, Color color) {
		super(width, height);
		setFilled(true);
		setFillColor(color);
		setColor(color);
	}

}
