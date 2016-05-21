package saga.progetto.tesi.core;

public class Version
{
	private static final String FREE_SERVER_ADDR = "http://151.100.179.52:8085";
//	private static final String PRIZE_SERVER_ADDR = "http://151.100.179.85:8085";
	private static final String PRIZE_SERVER_ADDR = "http://127.0.0.1:8085";
	private static final String FREE_MAP_ADDR = "http://151.100.179.52:8085";
//	private static final String PRIZE_MAP_ADDR = "http://151.100.179.85:8085";
	private static final String PRIZE_MAP_ADDR = "http://127.0.0.1:8085";
	private static final String FREE_LADDER_ADDR = "http://151.100.179.52/leaderboard/TKT.html";
	private static final String PRIZE_LADDER_ADDR = "http://151.100.179.85/leaderboard/TKT.html";
	private static final String FREE_CONTACT_ADDR = "https://groups.google.com/forum/#!forum/the-knowledge-towers-forum";
	private static final String PRIZE_CONTACT_ADDR = "https://groups.google.com/forum/#!forum/the-knowledge-towers-support";
	private static final String FREE_WINDOW_PATH = "images/menu/free_window.png";
	private static final String PRIZE_WINDOW_PATH = "images/menu/prize_window.png";
	
	public static String getServerAddress()
	{
		return TheKnowledgeTowers.isFreeVersion ? FREE_SERVER_ADDR : PRIZE_SERVER_ADDR;
	}
	
	public static String getMapAddress()
	{
		return TheKnowledgeTowers.isFreeVersion ? FREE_MAP_ADDR : PRIZE_MAP_ADDR;
	}
	
	public static String getLadderAddress()
	{
		return TheKnowledgeTowers.isFreeVersion ? FREE_LADDER_ADDR : PRIZE_LADDER_ADDR;
	}
	
	public static String getContactAddress()
	{
		return TheKnowledgeTowers.isFreeVersion ? FREE_CONTACT_ADDR : PRIZE_CONTACT_ADDR;
	}
	
	public static String getWindowPath()
	{
		return TheKnowledgeTowers.isFreeVersion ? FREE_WINDOW_PATH : PRIZE_WINDOW_PATH;
	}
}
