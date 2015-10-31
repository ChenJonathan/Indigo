package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Bullet extends Projectile
{
	private double angle;
	private int timer;

	private final int DEFAULT = 0;
	private final int DEATH = 1;

	public static final int DAMAGE = 1;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 10;
	public static final double SPEED = 70;
	public static final int DURATION = 25;

	public Bullet(Entity entity, double x, double y, double velX, double velY, int dmg)
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

		timer = DURATION;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.BULLET), -1);
	}

	public void update()
	{
		if(currentAnimation != DEATH)
		{
			if(timer == 0)
			{
				dead = true;
			}

			super.update();
			timer--;
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
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
					(int)getWidth(), (int)getHeight(), null);
		}
		else if(getX() > 0 && getX() < stage.getMapX() && timer < DURATION)
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
		else
		{
			slopeAngle = slopeAngle >= 0? slopeAngle : slopeAngle + Math.PI;
			if(Math.abs(slopeAngle - angle) < Math.abs(slopeAngle + Math.PI - angle))
			{
				angle = slopeAngle;
			}
			else if(Math.abs(slopeAngle + 2 * Math.PI - angle) < Math.abs(slopeAngle + Math.PI - angle))
			{
				angle = slopeAngle;
			}
			else
			{
				angle = Math.PI + slopeAngle;
			}
		}

		die();
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}
}