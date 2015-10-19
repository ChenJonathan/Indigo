package indigo.Entity;

import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Core extends Entity
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;

	public static final double CORE_WIDTH = 100;
	public static final double CORE_HEIGHT = 130;
	public static final int BASE_HEALTH = 2000;

	public Core(Stage stage, double x, double y, int health)
	{
		super(stage, x, y, health);
		name = "the core";

		width = CORE_WIDTH;
		height = CORE_HEIGHT;

		pushability = 0;
		flying = false;
		frictionless = false;

		friendly = true;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.TURRET_IDLE), -1);
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
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)(getWidth()), (int)(getHeight()), null);
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}

	public void die()
	{
		if(currentAnimation != DEATH)
		{
			setAnimation(DEATH, ContentManager.getAnimation(ContentManager.TURRET_DEATH), 2);
		}
	}
}