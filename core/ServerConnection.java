package saga.progetto.tesi.core;

import java.util.HashMap;
import java.util.Map;
import playn.core.Image;
import playn.core.Json;
import playn.core.util.Callback;
import saga.progetto.tesi.core.reposity.DataQuality;
import saga.progetto.tesi.core.reposity.DataType;
import saga.progetto.tesi.core.reposity.Language;
import static playn.core.PlayN.*;

/**
 * The class that presents the connection to the Game with a Purpose server. A
 * {@code ServerConnection} can be used for multiple rounds of gameplay.
 * 
 * <p>
 * Server state is represented in terms of <i>namespaces</i> and <i>key-value
 * pairs</i>. Key-value pairs are simply strings that the client must interpret.
 * A namespace denotes the identity of the object (or abstraction) associated
 * with a particular set of key-value pairs. For example, each enemy character
 * could have a unique namespace (their enemy ID) and each enemy's state
 * (hitpoints, skills) is stored in a specific key-value pair.
 */
public class ServerConnection
{

	/**
	 * The URL at which the server API is located
	 */
	private final String serverUrl;

	/**
	 * The ID of the client
	 */
	private String clientId;

	/**
	 * The language of the client
	 */
	private final Language languageClient;

	/**
	 * The ID of the game being played
	 */
	private final String gameId;

	/**
	 * The session ID for this connection, provided by the server during
	 * {@link #init()}
	 */
	private String sessionId;
	
	/**
	 * Constructs the {@code ServerConnection} instance, directing all API calls
	 * to the server at the specified URL. This instance will then automatically
	 * attempt to login in to retrieve the session ID. Callers should ensure
	 * that {@link #isReady()} returns {@code true} before calling
	 * state-affecting methods.
	 * 
	 * @param serverUrl
	 *            a fully-qualified URL, such as
	 *            {@code http://151.100.179.253:8080}, which is used to access
	 *            game state
	 * @param gameId
	 *            the unique name of the game being played
	 */
	
	private boolean response;

	public ServerConnection(String serverUrl, String gameId)
	{
		this.serverUrl = serverUrl;
		this.gameId = gameId;
		this.setClientId(null);
		this.languageClient = Language.EN;
	}

	/**
	 * Constructs the {@code ServerConnection} instance, directing all API calls
	 * to the server at the specified URL. This instance will then automatically
	 * attempt to login in to retrieve the session ID. Callers should ensure
	 * that {@link #isReady()} returns {@code true} before calling
	 * state-affecting methods.
	 * 
	 * @param serverUrl
	 *            a fully-qualified URL, such as
	 *            {@code http://151.100.179.253:8080}, which is used to access
	 *            game state
	 * @param gameId
	 *            the unique name of the game being played
	 * @param cleintId
	 *            the unique name of the client
	 */
	public ServerConnection(String serverUrl, Language lang, String gameId, String clientId)
	{
		this.serverUrl = serverUrl;
		this.gameId = gameId;
		this.setClientId(clientId);
		this.languageClient = lang;
	}

	private StringBuilder getBaseData(String op)
	{
		return getBaseData(op, true);
	}

	/**
	 * Returns the URL prefix needed for all stateful API calls to the server.
	 * 
	 * @param op
	 *            the operation that the server is to perform, such as
	 *            {@code saveState}.
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	private StringBuilder getBaseUrl(String op)
	{
		return getBaseUrl(op, true);
	}

	/**
	 * Returns the URL prefix needed for all API calls to the server, with an
	 * optional flag to specify whether this call requires knowing the client's
	 * state.
	 * 
	 * @param op
	 *            the operation that the server is to perform, such as
	 *            {@code saveState}.
	 * @param isStatefulOp
	 *            if the API call specified in {@code op} requires having client
	 *            state on the server (that is, a session ID)
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */

	private StringBuilder getBaseData(String op, boolean isStatefulOp)
	{
		if (sessionId == null && isStatefulOp)
		{
			throw new IllegalStateException("Cannot issue calls to server prior to session beginning.  "
					+ "Please use ServerConnection.isReader() to test whether " + "methods can safely be called.");
		}

		return (isStatefulOp) ? new StringBuilder("sessionId=").append(sessionId) : new StringBuilder("gameId=").append(gameId);
	}

	private StringBuilder getBaseUrl(String op, boolean isStatefulOp)
	{
		if (sessionId == null && isStatefulOp)
		{
			throw new IllegalStateException("Cannot issue calls to server prior to session beginning.  "
					+ "Please use ServerConnection.isReader() to test whether " + "methods can safely be called.");
		}

		return new StringBuilder(serverUrl).append('/').append(op);
	}

	/**
	 * Saves a log message on the server, where each entry in {@code message}
	 * will be recorded as a separate <i>column</i> in the log file. Each log
	 * message is annotated with the date received and the IDs for the game,
	 * client, and session.
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 * @throws IllegalArgumentException
	 *             if message is of zero length
	 */
	public void log(String... message)
	{
		if (message.length == 0)
		{
			throw new IllegalArgumentException("Must supply at least one message to log");
		}
		StringBuilder url = getBaseUrl("log");
		StringBuilder data = getBaseData("log");

		for (int i = 0; i < message.length; ++i)
			data.append("&message").append(i).append('=').append(message[i]);

		// Escape spaces
		String safeUrl = data.toString().replaceAll("[\\s]+", "%20");

		net().post(url.toString(), safeUrl, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				t.printStackTrace();
			}

			public void onSuccess(String json)
			{
			}
		});
	}

	/**
	 * Retrieves the state of all namespaces on the server, passing these to the
	 * {@link Callback} as a mapping from the namespace to all of its key-value
	 * pairs.
	 * 
	 * @param namespaceToStateCallback
	 *            a {@link Callback} whose methods are called when the state of
	 *            all namespaces has been retrieved. Each namespace is a key in
	 *            the {@link Map} passed to {@code onSuccess} and its assocated
	 *            value is a mapping of all the key-values for that namespace.
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getState(Callback<Map<String, Map<String, String>>> namespaceToStateCallback)
	{
		getStateInternal("getState", namespaceToStateCallback, true);
	}

	/**
	 * Retrieves the state of all namespaces on the server that are shared
	 * between all clients, passing these to the {@link Callback} as a mapping
	 * from the namespace to all of its key-value pairs.
	 * 
	 * @param namespaceToStateCallback
	 *            a {@link Callback} whose methods are called when the state of
	 *            all namespaces has been retrieved. Each namespace is a key in
	 *            the {@link Map} passed to {@code onSuccess} and its assocated
	 *            value is a mapping of all the key-values for that namespace.
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getGlobalState(Callback<Map<String, Map<String, String>>> namespaceToStateCallback)
	{
		getStateInternal("getGlobalState", namespaceToStateCallback, false);
	}

	/**
	 * Retrieves the state of all namespaces using the specified REST API
	 * endpoint
	 * 
	 * @param restApiEndpoint
	 *            which method to call on the server side
	 * @param namespaceToStateCallback
	 *            a {@link Callback} whose methods are called when the state of
	 *            all namespaces has been retrieved. Each namespace is a key in
	 *            the {@link Map} passed to {@code onSuccess} and its assocated
	 *            value is a mapping of all the key-values for that namespace.
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	private void getStateInternal(String restApiEndpoint, final Callback<Map<String, Map<String, String>>> namespaceToStateCallback,
			boolean isStatefulOp)
	{

		StringBuilder url = getBaseUrl(restApiEndpoint, isStatefulOp);
		StringBuilder data = getBaseData(restApiEndpoint, isStatefulOp);

		String safeUrl = data.toString();

		net().post(url.toString(), safeUrl, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				namespaceToStateCallback.onFailure(t);
			}

			public void onSuccess(String json)
			{
				Map<String, Map<String, String>> namespaceToState = new HashMap<String, Map<String, String>>();
				Json.Object allState = json().parse(json);
				for (String namespace : allState.keys())
				{
					Json.Object nsStateObj = allState.getObject(namespace);
					Map<String, String> nsState = new HashMap<String, String>();
					for (String key : nsStateObj.keys())
						nsState.put(key, nsStateObj.getString(key));
					namespaceToState.put(namespace, nsState);
				}
				namespaceToStateCallback.onSuccess(namespaceToState);
			}
		});
	}

	/**
	 * Retrieves the state of the specified namespace on the server, passing the
	 * state to the {@link Callback} as a mapping of all the namespace's
	 * key-value pairs. If the namespace does not exist on the server, an empty
	 * {@code Map} is returned.
	 * 
	 * @param stateCallback
	 *            a {@link Callback} whose methods are called when the state of
	 *            the desired namespace has been retrieved. The callback is
	 *            passed the mapping of all the key-values for that namespace.
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getState(Callback<Map<String, String>> stateCallback, String namespace)
	{
		getStateInternal("getState", stateCallback, namespace, true);
	}

	/**
	 * Retrieves the state of the specified namespace on the server where the
	 * namespace is shared by all games, passing the state to the
	 * {@link Callback} as a mapping of all the namespace's key-value pairs. If
	 * the namespace does not exist on the server, an empty {@code Map} is
	 * returned.
	 * 
	 * @param stateCallback
	 *            a {@link Callback} whose methods are called when the state of
	 *            the desired namespace has been retrieved. The callback is
	 *            passed the mapping of all the key-values for that namespace.
	 * @param namepsace
	 *            a namespace that is shared by all game players
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getGlobalState(Callback<Map<String, String>> stateCallback, String namespace)
	{
		getStateInternal("getGlobalState", stateCallback, namespace, false);
	}

	/**
	 * Retrieves the state of the specified namespace on the server, passing the
	 * state to the {@link Callback} as a mapping of all the namespace's
	 * key-value pairs. If the namespace does not exist on the server, an empty
	 * {@code Map} is returned.
	 * 
	 * @param restApiEndpoint
	 *            which method to call on the server side
	 * @param stateCallback
	 *            a {@link Callback} whose methods are called when the state of
	 *            the desired namespace has been retrieved. The callback is
	 *            passed the mapping of all the key-values for that namespace.
	 * @param isStatefulOp
	 *            {@code true} if the operation being performed requires
	 *            accessing user-specific state (that is, requires a session
	 *            key)
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	private void getStateInternal(String restApiEndpoint, final Callback<Map<String, String>> stateCallback, String namespace,
			boolean isStatefulOp)
	{

		StringBuilder url = getBaseUrl(restApiEndpoint, isStatefulOp);
		StringBuilder data = getBaseData(restApiEndpoint, isStatefulOp);

		data.append("&namespace=").append(namespace);
		String safeData = data.toString().replaceAll("[\\s]+", "%20");

		// String url = serverUrl+"/"+restApiEndpoint;
		// String data =
		// "sessionId="+sessionId+"&"+"gameId="+gameId+"&"+"action=getGlobalSate"+"&"+"namespace="+namespace;

		net().post(url.toString(), safeData, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				stateCallback.onFailure(t);
			}

			public void onSuccess(String json)
			{
				Json.Object allState = json().parse(json);
				Map<String, String> nsState = new HashMap<String, String>();
				for (String key : allState.keys())
				{
					nsState.put(key, allState.getString(key));
				}
				stateCallback.onSuccess(nsState);
			}
		});
	}

	/**
	 * Retrieves the value of specified key in the specified namespace on the
	 * server, passing the key-value as a {@link Map} to the provided
	 * {@link Callback} when it has been retrieved from the server. If the
	 * namespace or key do not exist on the server, an empty {@code Map} is
	 * returned.
	 * 
	 * @param stateCallback
	 *            a {@link Callback} whose methods are called when the state of
	 *            the desired key and namespace has been retrieved. The callback
	 *            is passed the mapping of the single key-values for that
	 *            namespace.
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getState(Callback<String> stateCallback, String namespace, String key)
	{
		getStateInternal("getState", stateCallback, namespace, key, true);
	}

	/**
	 * Retrieves the value of specified key in the specified namespace on the
	 * server, passing the key-value as a {@link Map} to the provided
	 * {@link Callback} when it has been retrieved from the server. If the
	 * namespace or key do not exist on the server, an empty {@code Map} is
	 * returned.
	 * 
	 * @param stateCallback
	 *            a {@link Callback} whose methods are called when the state of
	 *            the desired key and namespace has been retrieved. The callback
	 *            is passed the mapping of the single key-values for that
	 *            namespace.
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getGlobalState(Callback<String> stateCallback, String namespace, String key)
	{
		getStateInternal("getGlobalState", stateCallback, namespace, key, false);
	}

	/**
	 * Retrieves the value of specified key in the specified namespace on the
	 * server from the appropriate REST API endpoint, passing the key-value as a
	 * {@link Map} to the provided {@link Callback} when it has been retrieved
	 * from the server. If the namespace or key do not exist on the server, an
	 * empty {@code Map} is returned.
	 * 
	 * @param restApiEndpoint
	 *            which method to call on the server side
	 * @param stateCallback
	 *            a {@link Callback} whose methods are called when the state of
	 *            the desired key and namespace has been retrieved. The callback
	 *            is passed the mapping of the single key-values for that
	 *            namespace.
	 * @param isStatefulOp
	 *            {@code true} if the operation being performed requires
	 *            accessing user-specific state (that is, requires a session
	 *            key)
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	private void getStateInternal(String restApiEndpoint, final Callback<String> stateCallback, String namespace, String key,
			boolean isStatefulOp)
	{

		StringBuilder url = getBaseUrl(restApiEndpoint, isStatefulOp);
		StringBuilder data = getBaseData(restApiEndpoint, isStatefulOp);

		data.append("&namespace=").append(namespace);
		data.append("&key=").append(key);
		String safeDate = data.toString().replaceAll("[\\s]+", "%20");

		net().post(url.toString(), safeDate, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				stateCallback.onFailure(t);
			}

			public void onSuccess(String json)
			{
				stateCallback.onSuccess(json);
			}
		});
	}

	/**
	 * @author daniele
	 * 
	 * @param stateCallback
	 *            , typePurpose (RELATIONS, IMAGES, SYNONYMS) typeSet
	 *            (TOVALIDATE, GOLD, NEGATIVE)
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getData(final Callback<Map<String, Map<String, String>>> namespaceToStateCallback, DataType typePurpose,
			DataQuality... typeSet)
	{
		getData(namespaceToStateCallback, typePurpose, 200, typeSet);
	}

	/**
	 * @author daniele
	 * 
	 * @param stateCallback
	 *            , typePurpose (RELATIONS, IMAGES, SYNONYMS) typeSet
	 *            (TOVALIDATE, GOLD, NEGATIVE) and Size to set
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getData(final Callback<Map<String, Map<String, String>>> namespaceToStateCallback, DataType typePurpose, int size,
			DataQuality... typeSet)
	{
		if (size < 1)
			throw new IllegalArgumentException("Size must be positive");

		StringBuilder url = getBaseUrl("getData");
		StringBuilder data = getBaseData("getData");

		data.append("&typePurpose=").append(typePurpose.toString());
		data.append("&numElements=").append(size);

		for (int i = 0; i < typeSet.length; ++i)
			data.append("&" + typeSet[i]).append('=').append(typeSet[i]);

		String safeUrl = url.toString();
		String safeData = data.toString();

		net().post(safeUrl, safeData, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				namespaceToStateCallback.onFailure(t);
			}

			public void onSuccess(String json)
			{
				Map<String, Map<String, String>> setObject = new HashMap<String, Map<String, String>>();
				Json.Object allState = json().parse(json);
				for (String namespace : allState.keys())
				{
					Json.Object nsStateObj = allState.getObject(namespace);
					Map<String, String> nsState = new HashMap<String, String>();
					for (String key : nsStateObj.keys())
						nsState.put(key, nsStateObj.getString(key));
					setObject.put(namespace, nsState);
				}
				namespaceToStateCallback.onSuccess(setObject);
			}
		});
	}

	/**
	 * @author daniele
	 * 
	 * @param stateCallback
	 *            , typePurpose (RELATIONS, IMAGES, SYNONYMS) typeSet
	 *            (TOVALIDATE, GOLD, NEGATIVE)
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getData(final Callback<Map<String, Map<String, String>>> namespaceToStateCallback, DataType typePurpose, String idTarget,
			DataQuality typeSet)
	{
		getData(namespaceToStateCallback, typePurpose, idTarget, 200, typeSet);
	}

	/**
	 * @author daniele
	 * 
	 * @param stateCallback
	 *            , typePurpose (RELATIONS, IMAGES, SYNONYMS) typeSet
	 *            (TOVALIDATE, GOLD, NEGATIVE) and Size to set
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void getData(final Callback<Map<String, Map<String, String>>> namespaceToStateCallback, DataType typePurpose, String idTarget,
			int size, DataQuality typeSet)
	{
		if (size < 1)
			throw new IllegalArgumentException("Size must be positive");

		StringBuilder url = getBaseUrl("getData");
		StringBuilder data = getBaseData("getData");

		data.append("&typePurpose=").append(typePurpose.toString());
		data.append("&idTarget=").append(idTarget);
		data.append("&numElements=").append(size);
		data.append("&" + typeSet).append('=').append(typeSet);

		String safeUrl = url.toString();
		String safeData = data.toString();

		net().post(safeUrl, safeData, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				namespaceToStateCallback.onFailure(t);
			}

			public void onSuccess(String json)
			{
				Map<String, Map<String, String>> setObject = new HashMap<String, Map<String, String>>();
				Json.Object allState = json().parse(json);
				for (String namespace : allState.keys())
				{
					Json.Object nsStateObj = allState.getObject(namespace);
					Map<String, String> nsState = new HashMap<String, String>();
					for (String key : nsStateObj.keys())
						nsState.put(key, nsStateObj.getString(key));
					setObject.put(namespace, nsState);
				}
				namespaceToStateCallback.onSuccess(setObject);
			}
		});
	}

	//
	// /**
	// * @author daniele
	// *
	// * @param stateCallback, typePurpose (RELATIONS, IMAGES, SYNONYMS) typeSet
	// (TOVALIDATE, GOLD, NEGATIVE)
	// *
	// * @throws IllegalStateException
	// * if the server connection has not been established yet
	// */
	// public void getData(final Callback<Map<String, Map<String, String>>>
	// namespaceToStateCallback, DataType typePurpose, DataQuality... typeSet)
	// {
	// StringBuilder url = getBaseUrl("getData");
	// url.append("&typePurpose=").append(typePurpose.toString());
	// for (int i = 0; i < typeSet.length; ++i)
	// url.append("&"+typeSet[i].toString()).append('=').append(typeSet[i].toString());
	//
	// String safeUrl = url.toString();
	//
	// net().get(safeUrl, new Callback<String>()
	// {
	// public void onFailure(Throwable t)
	// {
	// namespaceToStateCallback.onFailure(t);
	// }
	//
	// public void onSuccess(String json)
	// {
	// Map<String, Map<String, String>> setObject = new HashMap<String,
	// Map<String, String>>();
	// Json.Object allState = json().parse(json);
	// for (String namespace : allState.keys())
	// {
	// Json.Object nsStateObj = allState.getObject(namespace);
	// Map<String, String> nsState = new HashMap<String, String>();
	// for (String key : nsStateObj.keys())
	// nsState.put(key, nsStateObj.getString(key));
	// setObject.put(namespace, nsState);
	// }
	// namespaceToStateCallback.onSuccess(setObject);
	// }
	// });
	// }

	/**
	 * Stores the value of the specified key in the provided name space on the
	 * server. This method is equivalent to {@link #saveState(String,Map)} and
	 * is provided for convenience when only a single key needs to be updated.
	 * If multiple keys need to be updated, the {@code Map}-based method will be
	 * significantly more efficient than repeated calls to this method.
	 * 
	 * @param namespace
	 *            the name of the entity whose state is being recorded
	 * @param key
	 *            the key to save
	 * @param value
	 *            the value of
	 * @param key
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void saveState(String namespace, String key, String value)
	{
		saveStateInternal("saveState", namespace, key, value);
	}

	/**
	 * Stores the value of the specified key in the provided namespace on the
	 * server, which is shared by all game instances. This method is equivalent
	 * to {@link #saveGlobalState(String,Map)} and is provided for convenience
	 * when only a single key needs to be updated. If multiple keys need to be
	 * updated, the {@code Map}-based method will be significantly more
	 * efficient than repeated calls to this method.
	 * 
	 * @param namespace
	 *            the name of the entity whose state is being recorded
	 * @param key
	 *            the key to save
	 * @param value
	 *            the value of {@code key}
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void saveGlobalState(String namespace, String key, String value)
	{
		saveStateInternal("saveGlobalState", namespace, key, value);
	}

	/**
	 * Stores the value of the specified key in the provided namespace on the
	 * server, using the appropriate REST API endpoint.
	 * 
	 * @param namespace
	 *            the name of the entity whose state is being recorded
	 * @param key
	 *            the key to save
	 * @param value
	 *            the value of {@code key}
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	private void saveStateInternal(String restApiEndpoint, String namespace, String key, String value)
	{

		StringBuilder url = getBaseUrl(restApiEndpoint);
		StringBuilder data = getBaseData(restApiEndpoint);

		data.append("&namespace=").append(namespace);
		data.append("&").append(key);
		data.append("=").append(value);

		// Escape spaces
		String safeUrl = url.toString().replaceAll("[\\s]+", "%20");
		String safeData = data.toString().replaceAll("[\\s]+", "%20");

		net().post(safeUrl, safeData, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				t.printStackTrace();
			}

			public void onSuccess(String json)
			{
			}
		});
	}

	/**
	 * Stores the values of the two specified keys in the provided name space on
	 * the server. This method is equivalent to {@link #saveState(String,Map)}
	 * and is provided for convenience when two keys need to be updated. If more
	 * than two keys need to be updated, the {@code Map}-based method will be
	 * significantly more efficient than repeated calls to this method.
	 * 
	 * @param namespace
	 *            the name of the entity whose state is being recorded
	 * @param key1
	 *            the first key to save
	 * @param value1
	 *            the value of {@code key1}
	 * @param key2
	 *            the second key to save
	 * @param value2
	 *            the value of {@code key2}
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void saveState(String namespace, String key1, String value1, String key2, String value2)
	{
		saveStateInternal("saveState", namespace, key1, value1, key2, value2);
	}

	/**
	 * Stores the values of the two specified keys in the provided name space on
	 * the server. This method is equivalent to {@link #saveState(String,Map)}
	 * and is provided for convenience when two keys need to be updated. If more
	 * than two keys need to be updated, the {@code Map}-based method will be
	 * significantly more efficient than repeated calls to this method.
	 * 
	 * @param namespace
	 *            the name of the entity whose state is being recorded
	 * @param key1
	 *            the first key to save
	 * @param value1
	 *            the value of {@code key1}
	 * @param key2
	 *            the second key to save
	 * @param value2
	 *            the value of {@code key2}
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void saveGlobalState(String namespace, String key1, String value1, String key2, String value2)
	{
		saveStateInternal("saveGlobalState", namespace, key1, value1, key2, value2);
	}

	/**
	 * Stores the values of the two specified keys in the provided name space on
	 * the server using the appropriate REST API endpoint.
	 * 
	 * @param namespace
	 *            the name of the entity whose state is being recorded
	 * @param key1
	 *            the first key to save
	 * @param value1
	 *            the value of {@code key1}
	 * @param key2
	 *            the second key to save
	 * @param value2
	 *            the value of {@code key2}
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	private void saveStateInternal(String restApiEndpoint, String namespace, String key1, String value1, String key2, String value2)
	{

		StringBuilder url = getBaseUrl(restApiEndpoint);
		StringBuilder data = getBaseData(restApiEndpoint);

		data.append("&namespace=").append(namespace);
		data.append("&").append(key1);
		data.append("=").append(value1);
		data.append("&").append(key2);
		data.append("=").append(value2);

		// Escape spaces
		String safeUrl = url.toString().replaceAll("[\\s]+", "%20");
		String safeData = data.toString().replaceAll("[\\s]+", "%20");

		net().post(safeUrl, safeData, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				t.printStackTrace();
			}

			public void onSuccess(String json)
			{
			}
		});

	}

	/**
	 * Stores the state of multiple namespaces' states on the server. This
	 * method is implemented asynchronously and is not guaranteed to be atomic.
	 * 
	 * @param namespaceToState
	 *            a mapping from a namespace to the key-value pairs representing
	 *            its state
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void saveState(Map<String, Map<String, String>> namespaceToState)
	{
		// Break the call up into multiple calls to separate out the keys for
		// multiple name spaces
		for (Map.Entry<String, Map<String, String>> e : namespaceToState.entrySet())
		{
			saveState(e.getKey(), e.getValue());
		}
	}

	/**
	 * Stores the state of multiple namespaces' states on the server, where the
	 * namespaces and their state are shared by all games. This method is
	 * implemented asynchronously and is not guaranteed to be atomic.
	 * 
	 * @param namespaceToState
	 *            a mapping from a namespace to the key-value pairs representing
	 *            its state
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void saveGlobalState(Map<String, Map<String, String>> namespaceToState)
	{
		// Break the call up into multiple calls to separate out the keys for
		// multiple name spaces
		for (Map.Entry<String, Map<String, String>> e : namespaceToState.entrySet())
		{
			saveGlobalState(e.getKey(), e.getValue());
		}
	}

	/**
	 * Stores the values of all key-value pairs in the provided name space on
	 * the server. This method is the preferred way of updating multiple keys in
	 * bulk.
	 * 
	 * @param namespace
	 *            the name of the entity whose state is being recorded
	 * @param state
	 *            a mapping of keys and values to save
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void saveState(String namespace, Map<String, String> state)
	{
		saveStateInternal("saveState", namespace, state);
	}

	/**
	 * Stores the values of all key-value pairs in the provided namespace on the
	 * server, where the namespace and its state are shared by all instances of
	 * this game. This method is the preferred way of updating multiple keys in
	 * bulk.
	 * 
	 * @param namespace
	 *            the name of the entity whose state is being recorded
	 * @param state
	 *            a mapping of keys and values to save
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	public void saveGlobalState(String namespace, Map<String, String> state)
	{
		saveStateInternal("saveGlobalState", namespace, state);
	}

	/**
	 * Stores the values of all key-value pairs in the provided namespace on the
	 * server using the appropriate REST API endpoint.
	 * 
	 * @param restApiEndpoint
	 *            which method to call on the server side
	 * @param namespace
	 *            the name of the entity whose state is being recorded
	 * @param state
	 *            a mapping of keys and values to save
	 * 
	 * @throws IllegalStateException
	 *             if the server connection has not been established yet
	 */
	private void saveStateInternal(String restApiEndpoint, String namespace, Map<String, String> state)
	{

		StringBuilder url = getBaseUrl(restApiEndpoint);
		StringBuilder data = getBaseData(restApiEndpoint);

		data.append("&namespace=").append(namespace);

		for (Map.Entry<String, String> e : state.entrySet())
		{
			data.append('&').append(e.getKey()).append('=').append(e.getValue());
		}

		String safeUrl = url.toString().replaceAll(" ", "+");
		String safeData = data.toString().replaceAll(" ", "+");

		net().post(safeUrl, safeData, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				throw new Error(t);
			}

			public void onSuccess(String json)
			{
			}
		});
	}

	/**
	 * Returns an {@link Image} loaded from the server for the associated ID.
	 */
	public Image getImage(String imgId)
	{
		return assets().getRemoteImage(serverUrl + "/getImage?id=" + imgId);
	}

	/**
	 * Reads a text file that is stored on the server and returns the contexts
	 * of that file as a {@link String} to the {@code Callback}
	 * {@code whenLoaded}.
	 * 
	 * @param txtFileId
	 *            the name (or ID) of the text file the server.
	 * @param whenLoaded
	 *            a callback whose {@code onSuccess} method will be passed the
	 *            contents of the text file on the server.
	 */
	public void getText(String txtFileId, final Callback<String> whenLoaded)
	{
		StringBuilder url = new StringBuilder(serverUrl);
		url.append("/getText?").append("id=").append(txtFileId);
		String safeUrl = url.toString().replaceAll(" ", "+");

		net().get(safeUrl, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				whenLoaded.onFailure(t);
			}

			public void onSuccess(String text)
			{
				whenLoaded.onSuccess(text);
			}
		});
	}

	/**
	 * Returns {@true} if the server connection has been established and methods
	 * of this instance can be called without throwing a
	 * {@link IllegalStateException}. This method should <b>not</b> be polled;
	 * instead it should be checked once per {@code update} loop to see if the
	 * server connection is ready and the game can advance in state.
	 */
	public boolean isReady()
	{
		return sessionId != null;
	}
	
	public void register(String clientId, String password, final Callback<String> cb)
	{
		StringBuilder url = new StringBuilder(serverUrl).append("/1/register");
		StringBuilder data = new StringBuilder("clientId=").append(clientId)
				.append("&psw=").append(password);
		String safeUrl = url.toString().replaceAll("[\\s]+", "%20");
		String safeData = data.toString().replaceAll("[\\s]+", "%20");

		net().post(safeUrl, safeData, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				throw new Error(t);
			}

			public void onSuccess(String json)
			{
				cb.onSuccess(json);
			}
		});
	}

	public void loginIn(String clientId, String password, final InformationRetriever retriever, final Callback<String> cb)
	{
		StringBuilder url = new StringBuilder(serverUrl).append("/1/login");
		StringBuilder data = new StringBuilder("gameId=").append(gameId).append("&clientId=").append(clientId).append("&language=")
				.append(languageClient.toString()).append("&psw=").append(password);
		String safeUrl = url.toString().replaceAll("[\\s]+", "%20");
		String safeData = data.toString().replaceAll("[\\s]+", "%20");

		net().post(safeUrl, safeData, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				t.printStackTrace();
				throw new Error(t);
			}

			public void onSuccess(String json)
			{
				setResponse(true);
				Json.Object allState = json().parse(json);
				Map<String, String> nsState = new HashMap<String, String>();
				for (String key : allState.keys())
					nsState.put(key, allState.getString(key));

				ServerConnection.this.sessionId = nsState.get("sessionId");
				ServerConnection.this.setClientId(nsState.get("clientId"));
				retriever.sendNewLogin(sessionId);
				cb.onSuccess(json);
			}
		});
	}
	
	public void loginIn(String sessionId, final Callback<Map<String, String>> cb)
	{
		StringBuilder url = new StringBuilder(serverUrl).append("/1/login");
		StringBuilder data = new StringBuilder("gameId=").append(gameId).append("&copySession=").append(sessionId).append("&language=")
				.append(languageClient.toString());
		String safeUrl = url.toString().replaceAll("[\\s]+", "%20");
		String safeData = data.toString().replaceAll("[\\s]+", "%20");

		net().post(safeUrl, safeData, new Callback<String>()
		{
			public void onFailure(Throwable t)
			{
				throw new Error(t);
			}

			public void onSuccess(String json)
			{
				Json.Object allState = json().parse(json);
				Map<String, String> nsState = new HashMap<String, String>();
				for (String key : allState.keys())
					nsState.put(key, allState.getString(key));

				ServerConnection.this.sessionId = nsState.get("sessionId");
				ServerConnection.this.setClientId(nsState.get("clientId"));
				cb.onSuccess(nsState);		
			}
		});
	}

	public boolean response()
	{
		return response;
	}

	public void setResponse(boolean response)
	{
		this.response = response;
	}

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

}