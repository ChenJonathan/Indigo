package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class GeyserParticle extends Projectile
{
	private final int DEFAULT = 0;
	
	public static final int DAMAGE = 0; //4
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
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)getWidth(), (int)getHeight(), null);
	}
	
	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}
	
	public void collide(Entity ent)
	{
		if(stage.getTime() % 2 == 0 && !ent.isDodging())
		{
			ent.mark();
			ent.setHealth(ent.getHealth() - damage);
			
			if(ent.isFlying())
			{
				ent.setY(ent.getY() - ent.getMovability() * 3 / 4); // Arbitrary scale value
			}
			else
			{
				ent.setVelY(ent.getVelY() - ent.getMovability() / 10); // Arbitrary scale value
				ent.removeGround();
			}
		}
	}
	
	// Not used
	public void collide(Wall wall) { }

	// Not used
	public boolean isActive()
	{
		return true;
	}
	
	// Not used
	public void die() { }
}