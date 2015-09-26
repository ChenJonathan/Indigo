package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class GeyserBase extends Projectile
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	private final double MOVE_SPEED = 60; // Maximum movement speed of the geyser base

	public final static int WIDTH = 100;
	public final static int HEIGHT = 100;

	public GeyserBase(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = false;
		flying = true;

		setAnimation(DEFAULT, Content.GEYSER_BASE, -1);
	}

	public void update()
	{
		if(currentAnimation != DEATH)
		{
			super.update();

			setX(Math.max(getX(), getWidth() / 2));
			setX(Math.min(getX(), stage.getMapX() - getWidth() / 2));

			setVelX((stage.getMouseX() - getX()) / 10);
			if(getVelX() > MOVE_SPEED)
			{
				setVelX(MOVE_SPEED);
			}
			else if(getVelX() < -MOVE_SPEED)
			{
				setVelX(-MOVE_SPEED);
			}
		}
		else
		{
			animation.update();
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)getWidth(), (int)getHeight(), null);
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	// Not used
	public void collide(Entity ent)
	{
	}

	// Not used
	public void collide(Wall wall)
	{
	}

	public boolean isActive()
	{
		return false;
	}
}