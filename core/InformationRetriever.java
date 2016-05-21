package saga.progetto.tesi.core;

public interface InformationRetriever
{

	/**
	 * Restituisce il sessionId per fare il login automatico o null se il login
	 * non è stato ancora fatto
	 */
	public String isUserLogged();

	public String getServerAddress();

	public String getGameName();

	public void sendNewLogin(String id);
}
