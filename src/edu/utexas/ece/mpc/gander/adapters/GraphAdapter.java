package edu.utexas.ece.mpc.gander.adapters;

import edu.utexas.ece.mpc.stdata.vertices.Datum;

public interface GraphAdapter<T, V extends Datum> {

	/**
	 * Serializes a piece of application data to graph format.
	 * 
	 * @param appData
	 *            the application data to serialize.
	 * @return a serialized graph data object.
	 */
	public V serialize(T appData);

	/**
	 * Deserializes a graph data object into a piece of application data.
	 * 
	 * @param graphData
	 *            a graph data object.
	 * @return a deserialized piece of application data.
	 */
	public T deserialize(V graphData);

}
