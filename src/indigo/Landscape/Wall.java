package indigo.Landscape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Wall implements Land
{
	protected String name;
	private Line2D.Double line;
	
	private double slope;
	private double minX;
	private double maxX;
	
	private boolean horizontal;

	protected boolean killsEntities; // Kills all entities
	protected boolean killsSolidEntities; // Kills solid entities
	protected boolean killsProjectiles; // Kills all projectiles
	protected boolean killsSolidProjectiles; // Kills solid projectiles
	
	public Wall(double x1, double y1, double x2, double y2)
	{
		name = "a wall";
		line = new Line2D.Double(x1, y1, x2, y2);
		
		slope = (y2 - y1) / (x2 - x1);
		minX = Math.min(x1, x2);
		maxX = Math.max(x1, x2);
		
		// Checks if angle is greater or less than 45 degrees
		horizontal = false;
		if(Math.abs(x2 - x1) >= Math.abs(y2 - y1))
		{
			horizontal = true;
		}
		
		killsProjectiles = false;
		killsSolidProjectiles = false;
		killsEntities = false;
		killsSolidEntities = false;
	}
	
	public void render(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.draw(line);
	}
	
	public String getName()
	{
		return name;
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
	
	public boolean isHorizontal()
	{
		return horizontal;
	}
	
	public boolean killsEntities()
	{
		return killsEntities;
	}
	
	public boolean killsSolidEntities()
	{
		return killsSolidEntities;
	}
	
	public boolean killsProjectiles()
	{
		return killsProjectiles;
	}
	
	public boolean killsSolidProjectiles()
	{
		return killsSolidProjectiles;
	}
}