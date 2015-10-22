package indigo.Projectile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Stage.Stage;

public class SteelBeam extends Projectile
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	public static final int DAMAGE = 15;
	public static final int WIDTH = 50;
	public static final int HEIGHT = 50;
	public static final double SPEED = 50;
	
	public SteelBeam(Stage stage, double x, double y)
	{
		this(stage, x, y, 0, 0, DAMAGE);
	}
	
	public SteelBeam(Stage stage, double x, double y, double velX, double velY, int dmg)
	{
		super(stage, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = false;
	}

	public void update()
	{
		// TODO: animation stuff
		super.update();
	}

	public void render(Graphics2D g)
	{
		// TODO: Animation stuff
		g.fillRect((int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)getWidth(), (int)getHeight());
	}
	
	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
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
		die();
	}
	
	public boolean isActive()
	{
		return currentAnimation != DEATH; // TODO: Fix if more animations are added
	}
}