package edu.utexas.ece.mpc.gander.network;

import android.content.Context;
import edu.utexas.ece.mpc.gander.adapters.NetworkAdapter;

public abstract class NetworkIO<T, N> implements NetworkOutput<T> {

	protected Context mContext;
	protected NetworkInputListener<T> mNetworkInputListener;

	protected NetworkAdapter<T, N> mAdapter;

	public NetworkIO(Context context,
			NetworkInputListener<T> networkInputListener,
			NetworkAdapter<T, N> adapter) {
		mContext = context;
		mNetworkInputListener = networkInputListener;
		mAdapter = adapter;
	}

	public abstract void shutdown();
}
