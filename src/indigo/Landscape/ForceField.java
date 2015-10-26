package indigo.Landscape;

import indigo.Stage.Stage;

public class ForceField extends Wall
{
	public ForceField(Stage stage, double x1, double y1, double x2, double y2)
	{
		super(stage, x1, y1, x2, y2);

		name = "a force field";

		blocksEntities = false;
		blocksNonsolidProjectiles = true;
		blocksSolidProjectiles = true;

		killsEntities = false;
		killsNonsolidProjectiles = false;
		killsSolidProjectiles = false;
	}
}