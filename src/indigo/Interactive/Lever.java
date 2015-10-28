package indigo.Interactive;

import indigo.Entity.Player;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class Lever extends Interactive
{
	private int id;
	
	private boolean activated;
	private boolean recentlyActivated;
	
	private final int IDLE = 0;
	private final int DEATH = 2;

	public final static int WIDTH = 50;
	public final static int HEIGHT = 50;

	public Lever(Stage stage, double x, double y, int id, boolean activated)
	{
		super(stage, x, y);
		width = WIDTH;
		height = HEIGHT;
		
		this.id = id;
		this.activated = activated;

		setAnimation(IDLE, ContentManager.getAnimation(ContentManager.HEALTH_PICKUP_IDLE), 3);
	}

	public void update()
	{
		super.update();
		
		if(recentlyActivated && !player.intersects(this))
		{
			recentlyActivated = false;
		}

		if(currentAnimation == DEATH)
		{
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)getWidth(), (int)getHeight(), null);
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void activate(Player player)
	{
		if(recentlyActivated)
		{
			return;
		}
		else
		{
			activated = !activated;
			recentlyActivated = true;
		}
		
		for(Interactive interactive : stage.getInteractives())
		{
			if(interactive instanceof Gate)
			{
				Gate gate = (Gate)interactive;
				if(gate.id() == id)
				{
					gate.toggle();
				}
			}
		}
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}

	public void die()
	{
		setAnimation(DEATH, ContentManager.getAnimation(ContentManager.HEALTH_PICKUP_DEATH), 2);
	}
}