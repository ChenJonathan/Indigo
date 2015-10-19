package indigo.Landscape;

import indigo.Stage.Stage;

public class SpikePit extends Wall
{
	public SpikePit(Stage stage, double x1, double y1, double x2, double y2)
	{
		super(stage, x1, y1, x2, y2);

		name = "a spike pit";

		blocksEntities = true;
		blocksNonsolidProjectiles = false;
		blocksSolidProjectiles = true;

		killsEntities = true;
		killsNonsolidProjectiles = false;
		killsSolidProjectiles = false;
	}
}