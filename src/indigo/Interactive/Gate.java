package indigo.Interactive;

import indigo.Entity.Player;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Gate extends Interactive
{
	private int id;
	private boolean open;

	private Wall wall;

	private final int IDLE = 0;
	private final int SPAWN = 1;
	private final int DEATH = 2;

	public final static int HEALTH = 100;
	public final static int WIDTH = 30;
	public final static int HEIGHT = 200;

	public Gate(Stage stage, double x, double y, int id, boolean open)
	{
		super(stage, x, y);
		width = WIDTH;
		height = HEIGHT;

		this.id = id;
		this.open = open;

		wall = new Wall(stage, x, y - getHeight() / 2, x, y + getHeight() / 2);
		stage.getLandscape().add(wall);
	}

	public void update()
	{
		super.update();

		if(currentAnimation == SPAWN)
		{
			if(animation.hasPlayedOnce())
			{
				setAnimation(IDLE, ContentManager.getAnimation(ContentManager.HEALTH_PICKUP_IDLE), 6);
			}
		}
		else if(currentAnimation == DEATH)
		{
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawRect((int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)getWidth(), (int)getHeight());
	}

	public int id()
	{
		return id;
	}
	
	public void toggle()
	{
		open = !open;
		if(!open)
		{
			stage.getLandscape().remove(wall);
		}
		else
		{
			stage.getLandscape().add(wall);
		}
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	// Not used
	public void activate(Player player)
	{

	}

	public boolean isActive()
	{
		return true;
	}

	// Not used
	public void die()
	{

	}
}