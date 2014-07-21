package edu.utexas.ece.mpc.gander.network;

public interface NetworkInputListener {

	/**
	 * Called when data has been received from the network.
	 * 
	 * @param source
	 *            the MAC address of the sender.
	 * @param data
	 *            the received data object.
	 */
	public <T> void receivedData(String source, T data);
}
