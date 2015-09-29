package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class GeyserParticle extends Projectile
{
	// Animation
	private final int DEFAULT = 0;

	public static final int DAMAGE = 2;
	public static final int WIDTH = 80;
	public static final int HEIGHT = 50;
	public static final double SPEED = 40;

	public GeyserParticle(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = false;
		flying = true;

		setAnimation(DEFAULT, Content.GEYSER, -1);
	}

	public void update()
	{
		super.update();
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

	public void collide(Entity ent)
	{
		if(!ent.isDodging() && ent.getY() >= getY() + getVelY() && ent.getY() < getY() - getVelY())
		{
			ent.mark();
			ent.setHealth(ent.getHealth() - damage);

			ent.setVelY(ent.getVelY() - ent.getPushability() / 3); // Arbitrary scale value
		}
	}

	// Not used
	public void collide(Wall wall)
	{
	}

	// Not used
	public boolean isActive()
	{
		return true;
	}
}