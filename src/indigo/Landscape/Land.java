package indigo.Landscape;

import java.awt.geom.Line2D;

public interface Land
{
	Line2D.Double getLine();
	
	double getSlope();
	double getMinX();
	double getMaxX();
	
	double getSurface(double x);
}