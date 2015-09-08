package indigo.Landscape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Platform implements Land
{
	private double slope;
	private double minX;
	private double maxX;
	
	private Line2D.Double line;
	
	public Platform(double x1, double y1, double x2, double y2)
	{
		line = new Line2D.Double(x1, y1, x2, y2);
		
		slope = (y2 - y1) / (x2 - x1);
		minX = Math.min(x1, x2);
		maxX = Math.max(x1, x2);
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.BLUE);
		g.draw(line);
	}
	
	public Line2D.Double getLine()
	{
		return line;
	}
	
	public double getSlope()
	{
		return 0;
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
	
	/*
	public double getSurface(double x)
	{
		return y - 1;
	}
	*/
}