package indigo.Landscape;

import indigo.Stage.Stage;

import java.awt.Color;
import java.awt.Graphics2D;

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

	public Wall(Stage stage, double x1, double y1, double x2, double y2)
	{
		super(stage, x1, y1, x2, y2);
		
		name = "a wall";

		// Checks if angle is greater or less than 45 degrees
		horizontal = (Math.abs(x2 - x1) >= Math.abs(y2 - y1))? true : false;

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