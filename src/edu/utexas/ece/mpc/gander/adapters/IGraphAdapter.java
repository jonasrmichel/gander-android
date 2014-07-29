package edu.utexas.ece.mpc.gander.adapters;

import edu.utexas.ece.mpc.gander.graph.SpaceTimePosition;
import edu.utexas.ece.mpc.stdata.factories.IDatumFactory;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;

public interface IGraphAdapter<T, D extends DatumVertex> extends
		IDatumFactory<D> {

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
	 * Serializes a piece of application data to graph format, storing it in the
	 * graph database, and associates the provided rule with the graph instance
	 * of the data.
	 * 
	 * @param appData
	 *            the application data to serialize.
	 * @param trajectory
	 *            any spatiotemporal metadata assocaited with the applciation
	 *            data.
	 * @param rules
	 *            unregistered rules to associate with the graph instance of the
	 *            provided data.
	 * @return the serialized graph instance of the data object.
	 */
	public D serialize(T appData, SpaceTimePosition[] trajectory, Rule... rules);

	/**
	 * Deserializes a graph data object into a piece of application data.
	 * 
	 * @param graphData
	 *            a graph data object.
	 * @return a deserialized piece of application data.
	 */
	public T deserialize(D graphData);

	/**
	 * Returns the representative graph instance of the provided application
	 * data object if it exists.
	 * 
	 * @param appData
	 *            a piece of application data.
	 * @return the representative graph instance of the application data if it
	 *         exists, null otherwise.
	 */
	public D find(T appData);

	/**
	 * Returns whether or not a piece of application data and a piece of graph
	 * data are "equivalent."
	 * 
	 * @param appData
	 *            an application data object.
	 * @param graphData
	 *            a graph data object.
	 * @return true if the two data objects are "equivalent," false otherwise.
	 */
	public boolean equivalent(T appData, D graphData);
}
