package indigo.Landscape;

import java.awt.geom.Line2D;

public abstract class Land
{
	protected Line2D.Double line;

	protected double slope;
	protected double minX;
	protected double maxX;

	public Line2D.Double getLine()
	{
		return line;
	}

	public double getSlope()
	{
		return slope;
	}

	public double getMinX()
	{
		return minX;
	}

	public double getMaxX()
	{
		return maxX;
	}

	public double getSurface(double x)
	{
		return slope * (x - line.getX1()) + line.getY1() - 1;
	}
}