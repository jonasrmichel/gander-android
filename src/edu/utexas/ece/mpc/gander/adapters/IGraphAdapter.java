package edu.utexas.ece.mpc.gander.adapters;

import edu.utexas.ece.mpc.stdata.factories.IDatumFactory;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.Datum;

public interface IGraphAdapter<T, D extends Datum> extends IDatumFactory<D> {

	/**
	 * Returns the application data type of this adapter.
	 * 
	 * @return the application data type of this adapter.
	 */
	public Class<T> getApplicationDataType();

	/**
	 * Returns the graph data type of this adapter.
	 * 
	 * @return the graph data type of this adapter.
	 */
	public Class<D> getGraphDataType();

	/**
	 * Serializes a piece of application data to graph format and stores it in
	 * the graph database.
	 * 
	 * @param appData
	 *            the application data to serialize.
	 * @return the serialized graph instance of the data object.
	 */
	public D serialize(T appData);

	/**
	 * Serializes a piece of application data to graph format, storing it in the
	 * graph database, and associates the provided rule with the graph instance
	 * of the data.
	 * 
	 * @param appData
	 *            the application data to serialize.
	 * @param rule
	 *            a rule to associate with the graph instance of the provided
	 *            data.
	 * @return the serialized graph instance of the data object.
	 */
	public D serialize(T appData, Rule rule);

	/**
	 * Deserializes a graph data object into a piece of application data.
	 * 
	 * @param graphData
	 *            a graph data object.
	 * @return a deserialized piece of application data.
	 */
	public T deserialize(D graphData);

}
