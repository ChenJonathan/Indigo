package indigo.Landscape;

import java.awt.geom.Line2D;

public abstract class Land
{
	public abstract Line2D.Double getLine();

	public abstract double getSlope();

	public abstract double getMinX();

	public abstract double getMaxX();

	public abstract double getSurface(double x);
}