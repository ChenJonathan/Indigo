package indigo.Entity;

import indigo.Landscape.Wall;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Blockade extends Entity
{
	Wall[] walls; // TODO Offset when wall hitboxes are replaced with rectangles

	public static final double BLOCKADE_WIDTH = 200;
	public static final double BLOCKADE_HEIGHT = 200;
	public static final int BASE_HEALTH = 500;
	public static final int BASE_EXPERIENCE = 30;

	private static final int DEFAULT = 0;

	public Blockade(Stage stage, double x, double y)
	{
		this(stage, x, y, BASE_HEALTH, BASE_EXPERIENCE);
	}

	public Blockade(Stage stage, double x, double y, int health, int experience)
	{
		super(stage, x, y, health, experience);
		width = BLOCKADE_WIDTH;
		height = BLOCKADE_HEIGHT;

		pushability = 0;
		solid = false;
		flying = true;
		friendly = false;

		Entity player = stage.getPlayer();
		if(player.getX() + player.getWidth() / 2 > getX() - getWidth() / 2
				&& player.getX() - player.getWidth() / 2 < getX() + getWidth()
				&& player.getY() + player.getHeight() / 2 > getY() - getHeight()
				&& player.getY() - player.getHeight() / 2 < getY() + getHeight())
		{
			walls = new Wall[0];
			dead = true;
		}
		else
		{
			walls = new Wall[4];
			walls[0] = new Wall(stage, getX() - getWidth() / 2, getY() - getHeight() / 2, getX() + getWidth() / 2,
					getY() - getHeight() / 2);
			walls[1] = new Wall(stage, getX() - getWidth() / 2, getY() + getHeight() / 2, getX() + getWidth() / 2,
					getY() + getHeight() / 2);
			walls[2] = new Wall(stage, getX() - getWidth() / 2, getY() - getHeight() / 2, getX() - getWidth() / 2,
					getY() + getHeight() / 2);
			walls[3] = new Wall(stage, getX() + getWidth() / 2, getY() - getHeight() / 2, getX() + getWidth() / 2,
					getY() + getHeight() / 2);
		}

		for(Wall wall : walls)
		{
			stage.getWalls().add(wall);
		}
	}

	public void update()
	{
		super.update();
	}

	public void render(Graphics2D g)
	{
		g.fillRect((int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)(getWidth()),
				(int)(getHeight()));
		// g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
		// (int)(getWidth()), (int)(getHeight()), null);
	}

	public String getName()
	{
		return "a blockade";
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public boolean isActive()
	{
		return true;
	}

	public void die()
	{
		for(Wall wall : walls)
		{
			for(Entity ent : stage.getEntities())
			{
				if(ent.isGrounded() && ent.getGround().equals(wall))
				{
					ent.removeGround();
				}
			}

			stage.getWalls().remove(wall);
		}

		dead = true;
	}

	public boolean contains(Wall otherWall)
	{
		for(Wall wall : walls)
		{
			if(wall.equals(otherWall))
			{
				return true;
			}
		}
		return false;
	}
}