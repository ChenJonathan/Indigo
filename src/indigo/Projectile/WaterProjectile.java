package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class WaterProjectile extends Projectile
{
	private double angle;
	private int timer;

	private final int DEFAULT = 0;
	private final int DEATH = 1;
	private final int DEATH_WALL = 2;

	public static final int DAMAGE = 10;
	public static final int WIDTH = 80;
	public static final int HEIGHT = 73;
	public static final double SPEED = 70;
	public static final int DURATION = 20;

	public WaterProjectile(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = true;
		
		if(getVelX() >= 0)
		{
			angle = Math.atan(getVelY() / getVelX());
		}
		else
		{
			angle = Math.PI + Math.atan(getVelY() / getVelX());
		}
		timer = DURATION;

		setAnimation(DEFAULT, Content.WATER_PROJECTILE, -1);
	}

	public void update()
	{
		if(currentAnimation != DEATH && currentAnimation != DEATH_WALL)
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
			g.drawImage(animation.getImage(), (int)getX() - WIDTH / 2, (int)getY() - HEIGHT / 2, WIDTH, HEIGHT, null);
		}
		else if(getX() > 0 && getX() < stage.getMapX() && (timer < DURATION - 1 || currentAnimation == DEATH_WALL))
		{
			// Rotation breaks if x is negative
			g.rotate(angle, getX(), getY());
			g.drawImage(animation.getImage(), (int)getX() - WIDTH / 2, (int)getY() - HEIGHT / 2, WIDTH, HEIGHT, null);
			g.rotate(-angle, getX(), getY());
		}
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getHeight() / 2, getY() - getHeight() / 2, getHeight(), getHeight());
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
		// TODO Consider making this more logical
		double slopeAngle = Math.atan(-1 / wall.getSlope());
		if(Math.abs(slopeAngle) < 0.0001)
		{
			// For completely horizontal walls
			angle = Math.PI / 2 * getVelY() / Math.abs(getVelY());
		}
		else if(Math.abs(slopeAngle - angle) < Math.abs(slopeAngle + Math.PI - angle))
		{
			angle = slopeAngle;
		}
		else
		{
			angle = Math.PI + slopeAngle;
		}
		
		setAnimation(DEATH_WALL, Content.WATER_PROJECTILE_DEATH_WALL, 5);
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
			setAnimation(DEATH, Content.WATER_PROJECTILE_DEATH, 5);
		}
	}
}