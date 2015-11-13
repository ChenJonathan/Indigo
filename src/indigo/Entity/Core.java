package indigo.Entity;

import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Core extends Entity
{
	private final int IDLE = 0;
	private final int DEATH = 1;

	public static final double CORE_WIDTH = 124;
	public static final double CORE_HEIGHT = 162;
	public static final int BASE_HEALTH = 2000;

	public Core(Stage stage, double x, double y, int health)
	{
		super(stage, x, y, health, 0);

		width = CORE_WIDTH;
		height = CORE_HEIGHT;

		pushability = 0;
		solid = true;
		flying = false;
		frictionless = false;

		friendly = true;

		setAnimation(IDLE, ContentManager.getAnimation(ContentManager.CORE_IDLE), -1);
	}

	public void update()
	{
		if(currentAnimation == DEATH)
		{
			super.update();
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
			return;
		}

		super.update();
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2 + 30), null);
		
		super.render(g);
	}

	public String getName()
	{
		return "the core";
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 8, getY() - getHeight() / 2 + 30, getWidth() / 2, getHeight() - 30);
	}

	public boolean isActive()
	{
		return !isDead();
	}

	public void die()
	{
		if(currentAnimation != DEATH)
		{
			dead = true; // TODO Animation
		}
	}
}