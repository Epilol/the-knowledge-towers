package saga.progetto.tesi.entity.dynamicentity.enemy;

import pythagoras.f.IPoint;

public class Path
{
	private IPoint startPoint;
	private IPoint endPoint;
	
	public Path(IPoint startPoint, IPoint endPoint)
	{
		this.setStartPoint(startPoint);
		this.setEndPoint(endPoint);
	}
	
	public enum PathType
	{
		
	}

	public IPoint getStartPoint()
	{
		return startPoint;
	}

	public void setStartPoint(IPoint startPoint)
	{
		this.startPoint = startPoint;
	}

	public IPoint getEndPoint()
	{
		return endPoint;
	}

	public void setEndPoint(IPoint endPoint)
	{
		this.endPoint = endPoint;
	}
}
