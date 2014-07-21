package edu.utexas.ece.mpc.gander;

public interface GanderDelegate {
	/**
	 * Called when data has been received from the network.
	 * 
	 * @param source
	 *            MAC address of the sender.
	 * @param data
	 *            the received data object.
	 */
	public <T> void receivedData(String source, T data);
}
