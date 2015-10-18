package indigo.Projectile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

public class IceChainHook extends Projectile
{
	private boolean reverse;
	private int timer;

	private double angle;

	private Entity attached;

	// Animation
	private final int DEFAULT = 0;

	public static final int DAMAGE = 0;
	public static final int WIDTH = 160;
	public static final int HEIGHT = 73;
	public static final double SPEED = 70;
	public static final double EXTENSION_RANGE = 50;
	public static final int EXTEND_DURATION = 20; // Time before hook is recalled
	public static final int RETURN_DURATION = 30; // Time for hook to return

	public IceChainHook(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = true;
		flying = true;

		if(getVelX() >= 0)
		{
			angle = Math.atan(getVelY() / getVelX());
		}
		else
		{
			angle = Math.PI + Math.atan(getVelY() / getVelX());
		}

		reverse = false;
		timer = 0;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.ICICLE), -1);
	}

	public void update()
	{
		super.update();
		timer++;

		// Directional and movement calculations
		if(!reverse)
		{
			if(getVelX() >= 0)
			{
				angle = Math.atan(getVelY() / getVelX());
			}
			else
			{
				angle = Math.PI + Math.atan(getVelY() / getVelX());
			}

			if(timer == EXTEND_DURATION)
			{
				reverse();
			}
		}
		else
		{
			// Sets velocity so that the hook travels towards the player
			double dx = 0;
			double dy = stage.getPlayer().getY() - getY();

			if(getX() > stage.getPlayer().getX())
			{
				stage.getPlayer().setDirection(true);
				dx = stage.getPlayer().getX() + EXTENSION_RANGE - getX();
			}
			else
			{
				stage.getPlayer().setDirection(false);
				dx = stage.getPlayer().getX() - EXTENSION_RANGE - getX();
			}
			double scale = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

			setVelX(dx / scale * SPEED);
			setVelY(dy / scale * SPEED);

			if(getVelX() >= 0)
			{
				angle = Math.PI + Math.atan(getVelY() / getVelX());
			}
			else
			{
				angle = Math.atan(getVelY() / getVelX());
			}

			// If hook reaches player or time is up, remove the hook
			if(scale < SPEED)
			{
				dead = true;

				// Set entity next to player if the hook reaches the player
				if(attached != null)
				{
					if(getX() > stage.getPlayer().getX())
					{
						attached.setX(stage.getPlayer().getX() + EXTENSION_RANGE);
					}
					else
					{
						attached.setX(stage.getPlayer().getX() - EXTENSION_RANGE);
					}
					attached.setY(stage.getPlayer().getY() + stage.getPlayer().getHeight() / 2 - attached.getHeight()
							/ 2);

					attached.canAttack(true);
					attached.canMove(true);
				}
			}
			else if(timer == RETURN_DURATION)
			{
				dead = true;
			}
		}

		// Pulling the entity
		if(attached != null)
		{
			attached.setX(getX());
			attached.setY(getY());

			attached.setVelX(0);
			attached.setVelY(0);

			attached.removeGround();

			ArrayList<Wall> intersectedWalls = new ArrayList<Wall>();

			// Entity-wall: Colliding with and landing on walls
			for(Wall wall : stage.getWalls())
			{
				if(attached.intersects(wall) || (attached.isGrounded() && attached.getGround().equals(wall)))
				{
					intersectedWalls.add(wall);
				}
			}
			if(intersectedWalls.size() > 0)
			{
				stage.sortWallsByDistance(attached, intersectedWalls);

				for(Wall intersectedWall : intersectedWalls)
				{
					if(intersectedWall.killsEntities() && attached.isActive())
					{
						attached.die();
						stage.trackDeath(intersectedWall.getName(), attached);
					}
					if(intersectedWall.blocksEntities())
					{
						if(!intersectedWall.isHorizontal())
						{
							// Leftward collision into wall
							if(attached.isRightOfWall(intersectedWall))
							{
								while(attached.intersects(intersectedWall))
								{
									attached.setX(attached.getX() + Stage.PUSH_AMOUNT);
									attached.setVelX(Math.max(attached.getVelX(), 0));
								}
							}
							// Rightward collision into wall
							else
							{
								while(attached.intersects(intersectedWall))
								{
									attached.setX(attached.getX() - Stage.PUSH_AMOUNT);
									attached.setVelX(Math.min(attached.getVelX(), 0));
								}
							}
						}
						else
						{
							// Downward collision into wall
							if(attached.isAboveWall(intersectedWall))
							{
								while(attached.intersects(intersectedWall))
								{
									attached.setY(attached.getY() - Stage.PUSH_AMOUNT);
									attached.setVelY(Math.min(attached.getVelY(), 0));
								}
							}
							// Upward collision into wall
							else
							{
								while(attached.intersects(intersectedWall))
								{
									attached.setY(attached.getY() + Stage.PUSH_AMOUNT);
									attached.setVelY(Math.max(attached.getVelY(), 0));
								}
							}
						}
					}
				}
			}

			setX(attached.getX());
			setY(attached.getY());

			// Let go of entity if it dies
			if(!attached.isActive())
			{
				attached = null;
			}
		}
	}

	public void render(Graphics2D g)
	{
		// Draw ranges from 0 to 1 in intervals of 0.05
		g.setColor(new Color(0, 255, 255));
		g.drawLine((int)stage.getPlayer().getX(), (int)stage.getPlayer().getY(), (int)getX(), (int)getY());
		if(getX() > 0 && getX() < stage.getMapX())
		{
			// Rotation breaks if x is negative
			g.rotate(angle, getX(), getY());
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
					(int)getWidth(), (int)getHeight(), null);
			g.rotate(-angle, getX(), getY());
		}
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void collide(Entity ent)
	{
		if(!reverse)
		{
			reverse();
			if(ent.isPushable())
			{
				attached = ent;
				attached.mark();

				attached.canAttack(false);
				attached.canMove(false);
			}
		}
	}

	public void collide(Wall wall)
	{
		if(!reverse)
		{
			reverse();
		}
	}

	public void reverse()
	{
		reverse = true;
		solid = false;
		timer = 0;

		setVelX(0);
		setVelY(0);
	}

	public boolean isActive()
	{
		return true;
	}
}