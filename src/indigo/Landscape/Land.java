package indigo.Landscape;

import indigo.Stage.Stage;

import java.awt.geom.Line2D;

public abstract class Land
{
	protected Stage stage;
	
	protected Line2D.Double line;

	protected double slope;
	protected double minX;
	protected double maxX;

	public Land(Stage stage, double x1, double y1, double x2, double y2)
	{
		this.stage = stage;
		
		line = new Line2D.Double(x1, y1, x2, y2);

		slope = (y2 - y1) / (x2 - x1);
		minX = Math.min(x1, x2);
		maxX = Math.max(x1, x2);
	}
	
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