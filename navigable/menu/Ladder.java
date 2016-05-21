package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.platform;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Circle;
import pythagoras.f.Dimension;
import pythagoras.f.ICircle;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.core.Version;
import saga.progetto.tesi.job.Job;
import saga.progetto.tesi.media.Text;

public class Ladder
{
	private static final String LADDER_PATH =  "images/menu/ladder.png";
	private static final IPoint RANK_POINT = new Point(264.0f, 176.0f);
	private static final IPoint NAME_POINT = new Point(329.0f, 176.0f);
	private static final IPoint LADDER_RECORD_POINT = new Point(394.0f, 176.0f);
	private static final IPoint ACCURACY_POINT = new Point(478.0f, 176.0f);
	public static final IPoint BEST_SCORE_POINT = new Point(537.0f, 176.0f);
	public static final ICircle LADDER_CROSS = new Circle(new Point(565.0f, 101.0f), 12.0f);
	public static final IRectangle BUTTON = new Rectangle(new Point(348.0f, 439.0f), new Dimension(107.0f, 22.0f));
	private static final float HEIGHT_OFFSET = 2.0f;
	private static final int MAX_PLAYERS = 15;
	private static Ladder instance;
	private static Image ladderImage;
	
	private Set<LadderEntry> entries;
	private ImageLayer ladderLayer;
	private Text[][] textEntries = new Text[MAX_PLAYERS][5];
	
	private Ladder()
	{
		entries = new TreeSet<LadderEntry>();
		ladderLayer = graphics().createImageLayer(ladderImage);
		ladderLayer.setVisible(false);
		ladderLayer.setDepth(2.0f);
		graphics().rootLayer().add(ladderLayer);
	}
	
	public static Ladder getInstance()
	{
		if (instance == null)
			instance = new Ladder();
		
		return instance;
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		ladderImage = assets().getImage(LADDER_PATH);
		watcher.add(ladderImage);
	}
	
	public void addLadderEntry(String name, String entry)
	{
		String[] token = entry.split("_");
		
		if (token.length == 3)
			entries.add(new LadderEntry(name, token[0], token[1], token[2]));
	}
	
	private static class LadderEntry implements Comparable<LadderEntry>
	{
		private String name;
		private String ladderPoints;
		private String accuracy;
		private String bestScore;
		
		private LadderEntry(String name, String bestScore, String points, String accuracy)
		{
			this.name = name;
			this.ladderPoints = bestScore;
			this.accuracy = accuracy;
			this.bestScore = points;
		}
		
		public String getAccuracy()
		{
			return String.valueOf(Job.fixDecimal(Float.parseFloat(accuracy) * 100 - 1)) + "%";
		}
		
		public String getScore()
		{
			return String.valueOf(Float.parseFloat(bestScore)).replaceAll("\\..*$", "");
		}
		
		@Override
		public int compareTo(LadderEntry entry)
		{
			if (Integer.parseInt(ladderPoints) == Integer.parseInt(entry.ladderPoints))
			{
				if (Float.parseFloat(accuracy) == Float.parseFloat(entry.accuracy))
				{
					if (Float.parseFloat(bestScore) == Float.parseFloat(entry.bestScore))
						return name.compareTo(entry.name);
					
					return Float.parseFloat(bestScore) < Float.parseFloat(entry.bestScore) ? 1: -1;
				}
				
				return Float.parseFloat(accuracy) < Float.parseFloat(entry.accuracy) ? 1: -1;
			}
			
			return Integer.parseInt(ladderPoints) < Integer.parseInt(entry.ladderPoints) ? 1 : -1;
		}
	}
	
	public void setData(Map<String, String> result)
	{
		for (Map.Entry<String, String> player : result.entrySet())
			addLadderEntry(player.getKey(), player.getValue());
		
		int i = 0;
		
		for (LadderEntry entry : entries)
		{
			if (i < MAX_PLAYERS)
			{
				Text rank = new Text(String.valueOf(i + 1), Font.Style.PLAIN, 9, 0xFF000000);
				Text name = new Text(entry.name, Font.Style.PLAIN, 9, 0xFF000000);
				Text record = new Text(entry.ladderPoints, Font.Style.PLAIN, 9, 0xFF000000);
				Text accuracy = new Text(entry.getAccuracy(), Font.Style.PLAIN, 9, 0xFF000000);
				Text ladderRecord = new Text(entry.getScore(), Font.Style.PLAIN, 9, 0xFF000000);
				rank.setTranslation(RANK_POINT.add(-rank.width(), (rank.height() + HEIGHT_OFFSET) * i));
				name.setTranslation(NAME_POINT.add(-name.width(), (name.height() + HEIGHT_OFFSET) * i));
				record.setTranslation(BEST_SCORE_POINT.add(-record.width(), (record.height() + HEIGHT_OFFSET) * i));
				accuracy.setTranslation(ACCURACY_POINT.add(-accuracy.width(), (accuracy.height() + HEIGHT_OFFSET) * i));
				ladderRecord.setTranslation(LADDER_RECORD_POINT.add(-ladderRecord.width(), (ladderRecord.height() + HEIGHT_OFFSET) * i));
				rank.setDepth(3.0f);
				name.setDepth(3.0f);
				record.setDepth(3.0f);
				accuracy.setDepth(3.0f);
				ladderRecord.setDepth(3.0f);
				rank.init();
				name.init();
				record.init();
				accuracy.init();
				ladderRecord.init();
				textEntries[i][0] = rank;
				textEntries[i][1] = name;
				textEntries[i][2] = record;
				textEntries[i][3] = accuracy;
				textEntries[i][4] = ladderRecord;
				i++;
			}
		}
	}
	
	public void setVisible (boolean visible)
	{
		ladderLayer.setVisible(visible);
		
		if (textEntries.length > 0)
		{
			for (int i = 0; i < MAX_PLAYERS; i++)
				for (int j = 0; j < 5; j++)
					if (textEntries[i][j] != null)
						textEntries[i][j].setVisible(visible);
		}
	}
	
	public boolean visible()
	{
		return ladderLayer.visible();
	}
	
	public void clear()
	{
		ladderLayer.destroy();
		
		for (int i = 0; i < MAX_PLAYERS; i++)
			for (int j = 0; j < 5; j++)
				if (textEntries[i][j] != null)
					textEntries[i][j].destroy();
	}
	
	public boolean click(IPoint p)
	{
		return BUTTON.contains(p);
	}
	
	public void openURL()
	{
		platform().openURL(Version.getLadderAddress());
	}
	
	@Override
	public String toString()
	{
		String ladder = "";
		
		for (LadderEntry entry : entries)
		{
			ladder += "[" + entry.name + ", ";
			ladder += entry.ladderPoints + ", ";
			ladder += entry.getAccuracy() + ", ";
			ladder += entry.getScore() + "]";
			ladder += "\n";
		}
		
		return ladder;
	}
}
