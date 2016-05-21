package saga.progetto.tesi.core.annotation;

import saga.progetto.tesi.core.ServerConnection;
import saga.progetto.tesi.core.reposity.DataQuality;
import saga.progetto.tesi.core.reposity.DataType;

public class AnnotationProtocol
{

	private DataType typeAnnotation;
	private ServerConnection server;
	private String titleByAnnotation;
	private String synsetId;

	/**
	 * @param server
	 * @param typeAnnotation
	 * @param titleByAnnotation
	 *            = title of the word by annotation
	 * */
	public AnnotationProtocol(ServerConnection server, DataType typeAnnotation, Annotation annotation)
	{
		this.typeAnnotation = typeAnnotation;
		this.server = server;
		this.synsetId = annotation.getToValidateIdSynset();
		this.titleByAnnotation = annotation.getToValidateTitle();
		sendStart();
	}

	private void sendStart()
	{
		server.log(synsetId, typeAnnotation.toString(), titleByAnnotation, "START_ANNOTATION");
	}

	/**
	 * **/
	public void sendEnd(boolean winner, float reliability)
	{
		server.log(synsetId, typeAnnotation.toString(), titleByAnnotation, "END_ANNOTATION", String.valueOf(winner), String.valueOf(reliability));
	}

	/**
	 * **/
	public void sendAnnotation(String title, DataQuality set, boolean answer, String value)
	{
		server.log(typeAnnotation.toString(), titleByAnnotation, title, set.toString(), String.valueOf(answer), value);
	}
}