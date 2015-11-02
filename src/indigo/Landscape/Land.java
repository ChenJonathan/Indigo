package indigo.Landscape;

import indigo.Stage.Stage;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public abstract class Land
{
	protected Stage stage;

	protected Line2D.Double line;

	protected boolean horizontal;
	protected double slope;
	
	protected double minX;
	protected double maxX;
	
	public static final int LAND_THICKNESS = 30;

	public Land(Stage stage, double x1, double y1, double x2, double y2)
	{
		this.stage = stage;

		line = new Line2D.Double(x1, y1, x2, y2);

		horizontal = (Math.abs(x2 - x1) >= Math.abs(y2 - y1))? true : false;
		slope = (y2 - y1) / ((x2 - x1) == 0? 0.0000001 : (x2 - x1));
		minX = Math.min(x1, x2);
		maxX = Math.max(x1, x2);
	}

	public double getMinX()
	{
		return minX;
	}

	public double getMaxX()
	{
		return maxX;
	}

	public double getDeltaX()
	{
		return maxX - minX;
	}

	public double getDeltaY()
	{
		return Math.abs(line.getY1() - line.getY2());
	}

	public Line2D.Double getLine()
	{
		return line;
	}

	public double getSlope()
	{
		return slope;
	}

	public boolean isHorizontal()
	{
		return horizontal;
	}

	public double getLength()
	{
		return Math.sqrt(Math.pow(getDeltaX(), 2) + Math.pow(getDeltaY(), 2));
	}

	public Point2D.Double getIntersection(Line2D.Double line)
	{
		double xInt;
		double yInt;
		if(line.getX1() != line.getX2())
		{
			double slope = (line.getY2() - line.getY1()) / (line.getX2() - line.getX1());
			double wallYInt = getSlope() * -getLine().getX1() + getLine().getY1();
			double projYInt = -line.getX2() * slope + line.getY2();
			xInt = -(wallYInt - projYInt) / (getSlope() - slope);
			yInt = xInt * slope + projYInt;
		}
		else
		{
			xInt = line.getX2();
			yInt = getSlope() * (line.getX2() - getLine().getX1()) + getLine().getY1();
		}
		return new Point2D.Double(xInt, yInt);
	}

	public double getSurface(double x)
	{
		return slope * (x - line.getX1()) + line.getY1() - 1;
	}
}