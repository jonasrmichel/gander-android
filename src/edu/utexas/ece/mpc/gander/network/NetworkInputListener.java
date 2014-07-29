package edu.utexas.ece.mpc.gander.network;

import edu.utexas.ece.mpc.gander.graph.SpaceTimePosition;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePositionVertex;

public interface NetworkInputListener {

	/**
	 * Called when data has been received from the network.
	 * 
	 * @param source
	 *            the MAC address of the sender.
	 * @param type
	 *            the type of the received data object.
	 * @param data
	 *            the received data object.
	 * @param trajectory
	 *            the data's spatiotemporal trajectory.
	 * @param rules
	 *            any rules associated with this data object.
	 */
	public <T> void receivedData(String source, Class<T> type, T data,
			SpaceTimePosition[] trajectory, Rule... rules);
}
