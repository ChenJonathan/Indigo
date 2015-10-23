package indigo.Projectile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;

public class PulseWave extends Projectile
{
	private int timer;

	private final int DEFAULT = 0;

	public static final int DAMAGE = 20; // Will scale by distance
	public final static int WIDTH = 1000;
	public final static int HEIGHT = 1000;
	public final static double PUSHBACK = 50; // TODO: Change getWidth(), getHeight(), and PUSHBACK to suit the Pulse
												// Shot, keeping hitbox size in mind
	public final static int DURATION = 8;

	public PulseWave(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = false;
		flying = true;

		timer = DURATION;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.PULSE_WAVE), -1);
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
		// TODO: If necessary, change to be that of the pulse shot
		g.drawImage(animation.getImage(), (int)(getX() - (getWidth() / 2 * (1 - (double)timer / DURATION))),
				(int)(getY() - (getHeight() / 2 * (1 - (double)timer / DURATION))),
				(int)(getWidth() * (1 - (double)timer / DURATION)),
				(int)(getHeight() * (1 - (double)timer / DURATION)), null);
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	// Not used
	public void collide(Entity ent)
	{
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
