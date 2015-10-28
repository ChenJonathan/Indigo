package indigo.Entity;

import indigo.Interactive.Branch;
import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Tree extends Entity
{
	public static final double TREE_WIDTH = 150;
	public static final double TREE_HEIGHT = 600;
	public static final int BASE_HEALTH = 250;

	private static final int DEFAULT = 0;

	private Branch[] branches;
	private double[][] branchPosition;
	private int[] branchTimer;
	private boolean[] branchAlive;

	public Tree(Stage stage, double x, double y)
	{
		this(stage, x, y, BASE_HEALTH, new double[][] { {-175, -200}, {175, -200,}, {-175, 50}, {175, 50}});
	}

	public Tree(Stage stage, double x, double y, int health, double[][] branchData)
	{
		super(stage, x, y, health);
		width = TREE_WIDTH;
		height = TREE_HEIGHT;

		solid = false;
		flying = true;
		friendly = true;

		// Finding closest wall
		double minDistance = 500;
		Land closestLand = null;
		for(Wall wall : stage.getWalls())
		{
			double distance = wall.getLine().ptSegDist(x, y);
			if(distance < minDistance && wall.getSlope() == 0)
			{
				minDistance = distance;
				closestLand = wall;
			}
		}
		for(Platform plat : stage.getPlatforms())
		{
			double distance = plat.getLine().ptSegDist(x, y);
			if(distance < minDistance && plat.getSlope() == 0)
			{
				minDistance = distance;
				closestLand = plat;
			}
		}

		if(closestLand == null)
		{
			dead = true;
		}
		else
		{
			Point2D.Double intersection = closestLand.getIntersection(new Line2D.Double(getX(), getY(), getX(), getY()
					+ minDistance + getHeight()));

			// Move tree to ground
			setY(intersection.getY() - getHeight() / 2);

			// Check if tree is on land
			if(closestLand.getLine().ptSegDist(intersection) > 1)
			{
				dead = true;
			}
		}

		if(!isDead())
		{
			this.branches = new Branch[branchData.length];
			branchPosition = branchData;
			for(int i = 0; i < branches.length; i++)
			{
				branches[i] = new Branch(stage, getX() + branchData[i][0], getY() + branchData[i][1]);
				stage.getInteractives().add(branches[i]);
				branchPosition[i][0] = branches[i].getX();
				branchPosition[i][1] = branches[i].getY();
			}

			branchTimer = new int[branchData.length];
			branchAlive = new boolean[branchData.length];
			for(int i = 0; i < branchAlive.length; i++)
			{
				branchAlive[i] = true;
			}
		}

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.TREE_DEFAULT), -1);
	}

	public void update()
	{
		if(isDead())
		{
			return;
		}

		super.update();
		for(int i = 0; i < branches.length; i++)
		{
			if(branches[i] != null && this.branches[i].isDead())
			{
				branchAlive[i] = false;
				branches[i] = null;
			}
			if(branchAlive[i] == false)
			{
				branchTimer[i]++;
			}

			if(branchTimer[i] == 150)
			{
				branches[i] = new Branch(stage, branchPosition[i][0], branchPosition[i][1]);
				stage.getInteractives().add(branches[i]);
				branchTimer[i] = 0;
				branchAlive[i] = true;
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)(getWidth()), (int)(getHeight()), null);
	}
	
	public String getName()
	{
		return "a tree";
	}

	public Shape getHitbox()
	{
		return new Rectangle2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public boolean isActive()
	{
		return true;
	}

	public void die()
	{
		dead = true;
		for(int i = 0; i < this.branches.length; i++)
		{
			if(branchAlive[i] == true)
			{
				branches[i].die();
			}
		}

	}

}