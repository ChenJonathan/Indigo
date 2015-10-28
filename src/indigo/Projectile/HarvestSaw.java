package indigo.Projectile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;

public class HarvestSaw extends Projectile
{
	private int timer;

	// TODO: Animation variables
	public final static int DAMAGE = 5;
	public final static int WIDTH = 40;
	public final static int HEIGHT = 8;
	public final static int DURATION = 5;

	public HarvestSaw(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = false;
		flying = true;

		timer = DURATION;

		// Animation
	}

	public void update()
	{
		super.update();
		timer--;

		if(timer == 0)
		{
			die();
		}
	}

	public void render(Graphics2D g)
	{
		// TODO: Animation
	}
	
	public String getName()
	{
		return creator.getName();
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void collide(Entity ent) // Collide with tree
	{
		if(!ent.isDodging() && !ent.isBlocking(isFacingRight()))
		{
			ent.setHealth(ent.getHealth() - damage);
		}
	}

	public void collide(Wall wall)
	{
		// shouldn't die, because wall shouldn't be near tree
	}

	public boolean isActive()
	{
		return true;
	}

	public void die()
	{
		dead = true;
	}
}
