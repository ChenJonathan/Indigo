package indigo.Projectile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

public class IceChainParticle extends Projectile
{
	// Animation
	private final int DEFAULT = 0;
	
	public static final int DAMAGE = 0;
	public static final int WIDTH = 100;
	public static final int HEIGHT = 100;
	public static final double SPEED = 40;
	
	public IceChainParticle(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = false;
		flying = true;
		
		setAnimation(DEFAULT, Content.WATER_BALL, -1);
	}
	
	public void render(Graphics2D g) 
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)getWidth(), (int)getHeight(), null);
	}
	
	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}
	
	public void collide(Entity ent)
	{
		
	}
	public void collide(Wall wall)
	{
		
	}
	
	public boolean isActive()
	{
		return true;
	}
	public void die()
	{
		
	}
}