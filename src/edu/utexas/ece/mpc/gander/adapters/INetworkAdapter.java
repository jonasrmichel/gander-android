package edu.utexas.ece.mpc.gander.adapters;

import edu.utexas.ece.mpc.gander.network.NetworkMessage;

public interface INetworkAdapter<T, N> {

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
	 * @param message
	 *            a network message containing a piece of application data to
	 *            serialize.
	 * @return a serialized message object in network format.
	 */
	public N serialize(NetworkMessage<T> message);

	/**
	 * Deserializes a network format object into a network message containing a
	 * piece of application data.
	 * 
	 * @param serializedMessage
	 *            a network format object.
	 * @return a deserialized network message containing a piece of application
	 *         data.
	 */
	public NetworkMessage<T> deserialize(N serializedMessage);

}
