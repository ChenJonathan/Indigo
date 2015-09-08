package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class WaterProjectile extends Projectile
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;
	
	public final static int DAMAGE = 10;
	public final static int WIDTH = 50;
	public final static int HEIGHT = 50;
	public final static double SPEED = 60;
	
	public WaterProjectile(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = false;
		
		setAnimation(DEFAULT, Content.WATER_BALL, -1);
	}
	
	public void update()
	{
		if(currentAnimation != DEATH)
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
		g.drawImage(animation.getImage(), (int)getX() - WIDTH / 2, (int)getY() - HEIGHT / 2, WIDTH, HEIGHT, null);
	}
	
	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}
	
	public void collide(Entity ent)
	{
		if(!ent.isDodging())
		{
			ent.setHealth(ent.getHealth() - damage);
			die();
		}
	}
	
	public void collide(Wall wall)
	{
		die();
	}
	
	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}
	
	public void die()
	{
		if(currentAnimation != DEATH)
		{
			setAnimation(DEATH, Content.WATER_BALL_DEATH, 5);
			setVelX(0);
			setVelY(0);
		}
	}
}