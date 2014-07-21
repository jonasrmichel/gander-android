package edu.utexas.ece.mpc.gander.adapters;

public interface NetworkAdapter<T, N> {

	/**
	 * Returns the application data type of this adapter.
	 * 
	 * @return the application data type of this adapter.
	 */
	public Class<T> getApplicationDataType();

	/**
	 * Returns the network data type of this adapter.
	 * 
	 * @return the network data type of this adapter.
	 */
	public Class<N> getNetworkDataType();

	/**
	 * Serializes a piece of application data to network format.
	 * 
	 * @param appData
	 *            the application data to serialize.
	 * @return a serialized network data object.
	 */
	public N serialize(T appData);

	/**
	 * Deserializes a network data object into a piece of application data.
	 * 
	 * @param netData
	 *            a network data object.
	 * @return a deserialized piece of application data.
	 */
	public T deserialize(N netData);

}
