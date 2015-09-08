package indigo.Projectile;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Mortar extends Projectile
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;
	
	public static final int DAMAGE = 8;
	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	public static final double SPEED = 60;
	
	public Mortar(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = false;
		
		setAnimation(DEFAULT, Content.MORTAR, -1);
	}
	
	public void update()
	{
		if(currentAnimation == DEATH)
		{
			animation.update();
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
		}
		else
		{
			super.update();
		}
	}
	
	public void render(Graphics2D g) 
	{
		if(currentAnimation == DEATH)
		{
			g.drawImage(animation.getImage(), (int)getX() - WIDTH, (int)getY() - HEIGHT, WIDTH * 2, HEIGHT * 2, null);
		}
		else
		{
			g.drawImage(animation.getImage(), (int)getX() - WIDTH / 2, (int)getY() - HEIGHT / 2, WIDTH, HEIGHT, null);
		}
	}
	
	public Shape getHitbox()
	{
		if(currentAnimation == DEATH)
		{
			// Tweak these values 
			return new Ellipse2D.Double(getX() - getWidth(), getY() - getHeight(), getWidth() * 2, getHeight() * 2);
		}
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}
	
	public void collide(Entity ent)
	{
		if(currentAnimation == DEATH && animation.getFrame() > 2 && animation.getFrame() < 7 && !ent.isDodging())
		{
			ent.setHealth(ent.getHealth() - damage / 4);
		}
		else if(currentAnimation != DEATH && !ent.isDodging())
		{
			ent.setHealth(ent.getHealth() - damage);
			die();
		}
	}
	
	public void collide(Wall wall)
	{
		die();
	}

	// Not used
	public boolean isActive()
	{
		return true;
	}
	
	public void die()
	{
		if(currentAnimation != DEATH)
		{
			setAnimation(DEATH, Content.MORTAR_DEATH, 3);
			setVelX(0);
			setVelY(0);
		}
	}
}