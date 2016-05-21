package saga.progetto.tesi.core.annotation;

public class AnnotationStatistic
{
	// per pesare le immagini da validare raccolte e le immagini sbagliate raccolte
	private static final float ALPHA = 0.5f;
	// per pesare la storia passata con l'ultima partita
	private static final float BETA = 0.5f;
	private static final float THRESHOLD = 0.5f;
			
	private float reliability;
	
	// sbagliati presi
	private float falsePositive;
	// in forse presi
	private float truePositive;
	// correttamente lasciati
	private float trueNegative;
	
	private float positiveImageShown;
	private float negativeImageShown;

	public AnnotationStatistic(float reliability)
	{
		this.reliability = reliability;
	}

	public float getReliability()
	{
		return reliability;
	}

	public void setReliability(float reliability)
	{
		this.reliability = reliability;
	}

	public float getFalsePositive()
	{
		return falsePositive;
	}

	public void setFalsePositive(float falsePositive)
	{
		this.falsePositive = falsePositive;
	}

	public float getTruePositive()
	{
		return truePositive;
	}

	public void setTruePositive(float truePositive)
	{
		this.truePositive = truePositive;
	}

	public float getTrueNegative()
	{
		return trueNegative;
	}

	public void setTrueNegative(float trueNegative)
	{
		this.trueNegative = trueNegative;
	}

	public float getPositiveImageShown()
	{
		return positiveImageShown;
	}

	public void setPositiveImageShown(float positiveImageShown)
	{
		this.positiveImageShown = positiveImageShown;
	}

	public float getNegativeImageShown()
	{
		return negativeImageShown;
	}

	public void setNegativeImageShown(float negativeImageShown)
	{
		this.negativeImageShown = negativeImageShown;
	}
	
	public float score()
	{
		float averageVTaken = 1.0f;
		float averageBTaken = 1.0f;
		
		if (positiveImageShown > 0)
			averageVTaken = truePositive / positiveImageShown;
		
		if (negativeImageShown > 0)
			averageBTaken = 1 - falsePositive / negativeImageShown;
		
		return ALPHA * averageVTaken + (1 - ALPHA) * averageBTaken;
	}
	
	public boolean hasPassed()
	{
		return score() >= THRESHOLD;
	}
	
	public void updateReliability()
	{
		reliability = BETA * reliability + (1 - BETA) * score();
	}
}