package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class ElectricBall extends Projectile
{
	private final int DEFAULT = 0;
	private final int SPARK = 1;
	
	public final static int DAMAGE = 2;
	public final static int WIDTH = 50;
	public final static int HEIGHT = 50;
	public final static double SPEED = 25;
	
	public ElectricBall(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = false;
		flying = true;
		
		setAnimation(DEFAULT, Content.ELECTRIC_BALL, -1);
	}
	
	public void update()
	{
		if(animation.hasPlayedOnce())
		{
			setAnimation(DEFAULT, Content.ELECTRIC_BALL, -1);
		}
		
		super.update();
	}
	
	public void render(Graphics2D g) 
	{
		g.drawImage(animation.getImage(), (int) getX() - WIDTH / 2, (int) getY() - HEIGHT / 2, WIDTH, HEIGHT, null);
	}
	
	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}
	
	public void collide(Entity ent)
	{
		setAnimation(SPARK, Content.ELECTRIC_SPARK, 1);
		
		if(!ent.isDodging() && !ent.isBlocking(isFacingRight()))
		{
			ent.setHealth(ent.getHealth() - damage);
		}
	}
	
	// Not used
	public void collide(Wall wall) { }

	// Not used
	public boolean isActive()
	{
		return true;
	}
	
	public void die() 
	{
		dead = true;
	}
}