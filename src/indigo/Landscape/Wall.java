package indigo.Landscape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class Wall extends Land
{
	protected String name;

	private boolean horizontal;

	// Blocks movement for entities; triggers collide method for projectiles
	protected boolean blocksEntities;
	protected boolean blocksNonsolidProjectiles;
	protected boolean blocksSolidProjectiles;

	// Kills entities upon touch; triggers die method for projectiles
	protected boolean killsEntities;
	protected boolean killsNonsolidProjectiles;
	protected boolean killsSolidProjectiles;

	public Wall(double x1, double y1, double x2, double y2)
	{
		name = "a wall";
		line = new Line2D.Double(x1, y1, x2, y2);

		slope = (y2 - y1) / ((x2 - x1) == 0 ? 0.0000001 : (x2 - x1));
		minX = Math.min(x1, x2);
		maxX = Math.max(x1, x2);

		// Checks if angle is greater or less than 45 degrees
		horizontal = false;
		if(Math.abs(x2 - x1) >= Math.abs(y2 - y1))
		{
			horizontal = true;
		}

		blocksEntities = true;
		blocksNonsolidProjectiles = false;
		blocksSolidProjectiles = true;

		killsEntities = false;
		killsNonsolidProjectiles = false;
		killsSolidProjectiles = false;
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

	public boolean isHorizontal()
	{
		return horizontal;
	}

	public boolean blocksEntities()
	{
		return blocksEntities;
	}

	public boolean blocksNonsolidProjectiles()
	{
		return blocksNonsolidProjectiles;
	}

	public boolean blocksSolidProjectiles()
	{
		return blocksSolidProjectiles;
	}

	public boolean killsEntities()
	{
		return killsEntities;
	}

	public boolean killsNonsolidProjectiles()
	{
		return killsNonsolidProjectiles;
	}

	public boolean killsSolidProjectiles()
	{
		return killsSolidProjectiles;
	}
}