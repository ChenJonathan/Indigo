package indigo.Landscape;

import java.awt.geom.Line2D;

public class Platform extends Land
{
	public Platform(double x1, double y1, double x2, double y2)
	{
		line = new Line2D.Double(x1, y1, x2, y2);

		slope = (y2 - y1) / (x2 - x1);
		minX = Math.min(x1, x2);
		maxX = Math.max(x1, x2);
	}
}