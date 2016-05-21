package saga.progetto.tesi.entity.staticentity;

// lanciata quando si vuole muovere un'entità statica
public class StaticEntityException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public StaticEntityException()
	{
		super("Cannot modify the position of static entities.");
	}
}
