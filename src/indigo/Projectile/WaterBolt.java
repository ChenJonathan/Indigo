package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class WaterBolt extends Projectile
{
	private int timer;
	private double angle;

	private final int DEFAULT = 0;
	private final int DEATH = 1;
	private final int DEATH_WALL = 2;

	public static final int DAMAGE = 10;
	public static final int WIDTH = 80;
	public static final int HEIGHT = 73;
	public static final double SPEED = 60;
	public static final int DURATION = 25;

	public WaterBolt(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = true;

		// Calculates angle of projectile (0 to 2 pi)
		angle = Math.atan(getVelY() / getVelX());
		angle = getVelX() >= 0? angle : angle + Math.PI;
		angle = angle >= 0? angle : angle + 2 * Math.PI;

		timer = 0;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.WATER_BOLT), 1);
	}

	public void update()
	{
		if(currentAnimation != DEATH && currentAnimation != DEATH_WALL)
		{
			if(timer == DURATION)
			{
				dead = true;
			}

			super.update();
			timer++;
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
		if(currentAnimation == DEATH)
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), null);
		}
		else if(getX() > 0 && getX() < stage.getMapX() && (timer > 2 || currentAnimation == DEATH_WALL))
		{
			// Rotation breaks if x is negative
			g.rotate(angle, getX(), getY());
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), null);
			g.rotate(-angle, getX(), getY());
		}
	}

	public String getName()
	{
		return creator.getName();
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void collide(Entity ent)
	{
		if(!ent.isDodging())
		{
			if(!(ent.isBlocking(isFacingRight())))
			{
				ent.mark();
				ent.setHealth(ent.getHealth() - damage);
			}
			die();
		}
	}

	public void collide(Wall wall)
	{
		angle = Math.atan(-1 / wall.getSlope());
		double distance = wall.getLine().ptSegDist(getPrevX(), getPrevY());
		double newDistance = wall.getLine().ptSegDist(getPrevX() + Math.cos(angle), getPrevY() + Math.sin(angle));
		if(newDistance > distance)
		{
			angle += Math.PI;
		}

		setAnimation(DEATH_WALL, ContentManager.getAnimation(ContentManager.WATER_BOLT_DEATH_WALL), 5);
		die();
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH && currentAnimation != DEATH_WALL;
	}

	public void die()
	{
		setVelX(0);
		setVelY(0);

		if(currentAnimation != DEATH && currentAnimation != DEATH_WALL)
		{
			setAnimation(DEATH, ContentManager.getAnimation(ContentManager.WATER_BOLT_DEATH), 5);
		}
	}
}