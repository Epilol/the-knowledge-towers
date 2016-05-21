package saga.progetto.tesi.entity.dynamicentity;

/**
 * 
 * The enumeration {@code Direction} represents the direction of a {@link BoxEntity}.
 *
 */
public enum Direction 
{
	UP(0.0f, -1.0f, 1.0f, 3, 0.0f, 0.0), 
	TOP_RIGHT(1.0f, -1.0f, 0.707167811865f, 2, 0.785398163f, 45.0), 
	RIGHT(1.0f, 0.0f, 1.0f, 2, 1.570796327f, 90.0), 
	BOTTOM_RIGHT(1.0f, 1.0f, 0.707167811865f, 0, 2.35619449f, 135.0), 
	DOWN(0.0f, 1.0f, 1.0f, 0, 3.141592654f, 180.0),
	BOTTOM_LEFT(-1.0f, 1.0f, 0.707167811865f, 1, 3.926990817f, 225.0), 
	LEFT(-1.0f, 0.0f, 1.0f, 1, 4.71238898f, 270.0), 
	TOP_LEFT(-1.0f, -1.0f, 0.707167811865f, 3, 5.497787144f, 315.0),
	DEFAULT(0.0f, 0.0f, 0.0f, 0, 0.0f, 0.0);
	
	private float x;
	private float y;
	private float speedAdjustment;
	private int utilityIndex;
	private float angle;
	private double degree;
	
	Direction(float x, float y, float speedAdjustment, int utilityIndex, float angle, double degree)
	{
		this.x = x;
		this.y = y;
		this.speedAdjustment = speedAdjustment;
		this.utilityIndex = utilityIndex;
		this.angle = angle;
		this.degree = degree;
	}
	
	public float x()
	{
		return x;
	}
	
	public float y()
	{
		return y;
	}
	
	public float getSpeedAdjustment() 
	{
		return speedAdjustment;
	}
	
	public int getUtilityIndex()
	{
		return utilityIndex;
	}
	
	public float getAngle()
	{
		return angle;
	}
	
	public double getDegree()
	{
		return degree;
	}
	
	public static Direction getNextDirection(Direction direction)
	{
		switch(direction)
		{
			case BOTTOM_LEFT: return Direction.LEFT;
			case BOTTOM_RIGHT: return Direction.DOWN;
			case DOWN: return Direction.BOTTOM_LEFT;
			case LEFT: return Direction.TOP_LEFT;
			case RIGHT: return Direction.BOTTOM_RIGHT;
			case TOP_LEFT: return Direction.UP;
			case TOP_RIGHT: return Direction.RIGHT;
			case UP: return Direction.TOP_RIGHT;
			default: return Direction.UP;
		}
	}
	
	public static Direction getOppositeDirection(Direction direction)
	{
		switch(direction)
		{
			case BOTTOM_LEFT: return Direction.TOP_RIGHT;
			case BOTTOM_RIGHT: return Direction.TOP_LEFT;
			case DOWN: return Direction.UP;
			case LEFT: return Direction.RIGHT;
			case RIGHT: return Direction.LEFT;
			case TOP_LEFT: return Direction.BOTTOM_RIGHT;
			case TOP_RIGHT: return Direction.BOTTOM_LEFT;
			case UP: return Direction.DOWN;
			default: return Direction.UP;
		}
	}
	
}
