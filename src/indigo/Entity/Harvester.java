package indigo.Entity;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import indigo.Landscape.Land;
import indigo.Projectile.HarvesterProjectile;
import indigo.Stage.Stage;

public class Harvester extends Entity
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;
	private final Tree tree;
	
	public static final double HARVESTER_WIDTH = 100;
	public static final double HARVESTER_HEIGHT = 130;
	public static final int BASE_HEALTH = 250;// TODO: Change to harvester's stats
	public static final int RANGE = 50;// TODO: Balancing
	
	public void setHealth(int health)
	{
		super.setHealth(health);
	}
	
	public Harvester(Stage stage, double x, double y, int health, Tree tree)
	{
		super(stage, x, y, health);
		this.tree = tree;
		name = "a harvester";
		
		width = HARVESTER_WIDTH;
		height = HARVESTER_HEIGHT;
		
		pushability = 5;
		flying = false;
		frictionless = false;
		
		friendly = false;
		
		// TODO: setAnimation(DEAFULT, ContentManger.getAnimation)
	}
	
	public void update()
	{
		super.update();
		if(inRange() && canAttack())
		{
			//something about facing left or right, and then attacking
		}
	}
	
	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)(getWidth()), (int)(getHeight()), null);
	}
	
	public boolean inRange()
	{
		//in range is if harvester is within attacking range (distance) of tree on both x and y.
		boolean xInRange = Math.abs(tree.getX()-getX()) <= RANGE;
		boolean yInRange = (getY() - HARVESTER_HEIGHT >= tree.getY()-tree.HEIGHT) && (getY() + HARVESTER_HEIGHT <= tree.getY() + tree.HEIGHT);
		return xInRange && yInRange;
	}
	
	public void attack()
	{
		//attack tree
		if(stage.getTime() % 5 == 0)
		{
			//Summon projectile, it should never move, disappear after time
			stage.getProjectiles().add(new HarvesterProjectile(this, this.getX() + this.getWidth() / 2, this.getY(), 0, 0, HarvesterProjectile.DAMAGE));
		}
	}
	
	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}
	
	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}
	
	public void setGround(Land ground)
	{
		super.setGround(ground);
		canAttack(true);
	}
	
	public void removeGround()
	{
		super.removeGround();
		canAttack(false);
	}
	
	public void die()
	{
		// TODO
	}
}
