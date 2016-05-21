package saga.progetto.tesi.core.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import saga.progetto.tesi.core.reposity.DataQuality;

public class Annotation
{
	private String toValidateTitle;
	private String negativeTitle;
	private String goldTitle;
	private String toValidateGloss;
	private String negativeGloss;
	private String goldGloss;

	private String toValidateIdSynset;
	private String negativeIdSynset;
	private String goldIdSynset;
	private Map<String, String> toValidate;
	private Map<String, String> negative;
	private Map<String, String> gold;
	private List<String> toValidateIndex;
	private List<String> negativeIndex;
	private List<String> goldIndex;

	private void setParameters(DataQuality typeSet, Map<String, String> map)
	{

		if (typeSet.equals(DataQuality.TOVALIDATE))
		{
			this.toValidate = map;
			toValidateTitle = toValidate.remove("lemma");
			toValidateGloss = toValidate.remove("gloss");
			toValidateIdSynset = toValidate.remove("babelNetId");
			toValidateIndex = new ArrayList<String>(toValidate.keySet());

		}
		if (typeSet.equals(DataQuality.NEGATIVE))
		{
			this.negative = map;
			negativeTitle = negative.remove("lemma");
			negativeGloss = negative.remove("gloss");
			negativeIdSynset = negative.remove("babelNetId");
			negativeIndex = new ArrayList<String>(negative.keySet());

		}
		if (typeSet.equals(DataQuality.GOLD))
		{
			this.gold = map;
			goldTitle = negative.remove("lemma");
			goldGloss = negative.remove("gloss");
			goldIdSynset = negative.remove("babelNetId");
			goldIndex = new ArrayList<String>(gold.keySet());
		}
	}

	/**
	 * create the class with a single set
	 * **/
	public Annotation(DataQuality typeSet, Map<String, String> map)
	{
		setParameters(typeSet, map);
	}

	/**
	 * 
	 * **/
	public Annotation(Map<String, Map<String, String>> map)
	{
		for (Entry<String, Map<String, String>> mapLoad : map.entrySet())
			setParameters(DataQuality.valueOf(mapLoad.getKey()), mapLoad.getValue());
	}

	public void updateSet(DataQuality typeSet, Map<String, String> map)
	{
		setParameters(typeSet, map);
	}

	public String getToValidateTitle()
	{
		return toValidateTitle;
	}

	public String getNegativeTitle()
	{
		return negativeTitle;
	}

	public String getToValidateGloss()
	{
		return toValidateGloss;
	}

	public String getNegativeGloss()
	{
		return negativeGloss;
	}

	public Map<String, String> getToValidate()
	{
		return toValidate;
	}

	public Map<String, String> getNegative()
	{
		return negative;
	}

	public List<String> getToValidateIndex()
	{
		return toValidateIndex;
	}

	public String getGoldTitle()
	{
		return goldTitle;
	}

	public String getGoldGloss()
	{
		return goldGloss;
	}

	public String getToValidateIdSynset()
	{
		return toValidateIdSynset;
	}

	public String getNegativeIdSynset()
	{
		return negativeIdSynset;
	}

	public String getGoldIdSynset()
	{
		return goldIdSynset;
	}

	public Map<String, String> getGold()
	{
		return gold;
	}

	public List<String> getNegativeIndex()
	{
		return negativeIndex;
	}

	public List<String> getGoldIndex()
	{
		return goldIndex;
	}

	public String getRandomElement(DataQuality eSet)
	{
		if (eSet == DataQuality.TOVALIDATE)
			return toValidateIndex.get(new Random().nextInt(toValidateIndex.size()));
		if (eSet == DataQuality.NEGATIVE)
			return negativeIndex.get(new Random().nextInt(negativeIndex.size()));
		else
			return goldIndex.get(new Random().nextInt(goldIndex.size()));
	}

	public String getValueElement(DataQuality eSet, String element)
	{
		if (eSet == DataQuality.TOVALIDATE)
			return toValidate.get(element);
		if (eSet == DataQuality.NEGATIVE)
			return negative.get(element);
		else
			return gold.get(element);
	}
}