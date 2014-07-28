package edu.utexas.ece.mpc.gander.network;

import edu.utexas.ece.mpc.stdata.rules.Rule;

public interface NetworkInputListener {

	/**
	 * Called when data has been received from the network.
	 * 
	 * @param source
	 *            the MAC address of the sender.
	 * @param data
	 *            the received data object.
	 * @param rules
	 *            any rules associated with this data object.
	 */
	public <T> void receivedData(String source, T data, Rule... rules);
}
