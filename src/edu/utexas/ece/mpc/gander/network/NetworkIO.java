package edu.utexas.ece.mpc.gander.network;

import java.util.Map;

import android.content.Context;
import edu.utexas.ece.mpc.gander.adapters.INetworkAdapter;
import edu.utexas.ece.mpc.stdata.rules.Rule;

public abstract class NetworkIO {

	protected Context mContext;
	protected NetworkInputListener mNetworkInputListener;

	protected Map<Class, INetworkAdapter> mAdapters;

	public NetworkIO(Context context) {
		mContext = context;
	}

	public void setNetworkInputListener(
			NetworkInputListener networkInputListener) {
		mNetworkInputListener = networkInputListener;
	}

	public void setNetworkAdapters(Map<Class, INetworkAdapter> adapters) {
		mAdapters = adapters;
	}

	/**
	 * Sends network data via a network connection.
	 * 
	 * @param data
	 *            a piece of network data to send.
	 */
	protected abstract void sendData(Object data);

	/**
	 * Called to safely shutdown.
	 */
	public abstract void shutdown();

	/**
	 * Sends application data via a network connection.
	 * 
	 * @param type
	 *            the type of the data to send.
	 * @param data
	 *            a piece of application data to send.
	 * @param rules
	 *            any rules associated with this piece of application data.
	 */
	public <T> void sendData(Class<T> type, T data, Rule... rules) {
		// lookup the network adapter for this type of data
		INetworkAdapter adapter = mAdapters.get(type);

		// serialize the data and send it over the network
		sendData(adapter.serialize(new NetworkMessage<T>(data, rules)));
	}

	/**
	 * Called to inform the network input listener that data has been received
	 * from the network.
	 * 
	 * @param source
	 *            the MAC address of the sender of the received data.
	 * @param type
	 *            the type of the received data.
	 * @param data
	 *            the received network data object.
	 */
	protected <T, N> void receivedData(String source, Class<N> type, N data) {
		INetworkAdapter adapter = mAdapters.get(type);

		NetworkMessage<T> message = adapter.deserialize(data);
		mNetworkInputListener.receivedData(source, message.getPayload(),
				message.getRules());
	}
}
