package indigo.Projectile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

public class SteelBeam extends Projectile
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	public static final int DAMAGE = 15;
	public static final int WIDTH = 50;
	public static final int HEIGHT = 100;
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

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.STEEL_BEAM), -1);
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
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight()), null);
	}

	public String getName()
	{
		return "a steel beam";
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight(), getWidth(), getHeight());
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
		return currentAnimation != DEATH;
	}

	public void die()
	{
		setVelX(0);
		setVelY(0);

		if(currentAnimation != DEATH)
		{
			setAnimation(DEATH, ContentManager.getAnimation(ContentManager.STEEL_BEAM_DEATH), 1);
		}
	}
}