package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class FrostOrb extends Projectile
{
	private double angle;

	private final int DEFAULT = 0;
	private final int DEATH = 1;
	private final int DEATH_WALL = 2;

	public static final int DAMAGE = 150;
	public static final int WIDTH = 110;
	public static final int HEIGHT = 110;
	public static final double SPEED = 50;

	public FrostOrb(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = false;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.FROST_ORB), 2);
	}

	public void update()
	{
		if(currentAnimation != DEATH && currentAnimation != DEATH_WALL)
		{
			super.update();
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
		if(currentAnimation != DEATH_WALL)
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
					(int)getWidth(), (int)getHeight(), null);
		}
		else if(getX() > 0 && getX() < stage.getMapX() && currentAnimation == DEATH_WALL)
		{
			// Rotation breaks if x is negative
			g.rotate(angle, getX(), getY());
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
					(int)getWidth(), (int)getHeight(), null);
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
		double slopeAngle = Math.atan(-1 / wall.getSlope());
		if(Math.abs(slopeAngle) < 0.0001)
		{
			// For completely vertical walls
			angle = getVelX() > 0? 0 : Math.PI;
		}
		else if(Math.abs(slopeAngle - angle) < Math.abs(slopeAngle + Math.PI - angle))
		{
			angle = slopeAngle;
		}
		else
		{
			angle = Math.PI + slopeAngle;
		}

		// setAnimation(DEATH_WALL, ContentManager.getAnimation(ContentManager.WATER_PROJECTILE_DEATH_WALL), 5);
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

		width = 150;
		height = 150;
		
		if(currentAnimation != DEATH && currentAnimation != DEATH_WALL)
		{
			setAnimation(DEATH, ContentManager.getAnimation(ContentManager.WATER_BOLT_DEATH), 5);
		}
	}
}