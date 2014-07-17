package edu.utexas.ece.mpc.gander;

import java.util.Iterator;
import java.util.Map;

import stdata.IContextProvider;
import stdata.INetworkProvider;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.geo.Geoshape;
import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.tinkerpop.blueprints.Graph;

import edu.utexas.ece.mpc.gander.adapters.NetworkAdapter;
import edu.utexas.ece.mpc.gander.location.LocationHelper;
import edu.utexas.ece.mpc.gander.network.NetworkInputListener;
import edu.utexas.ece.mpc.gander.network.NetworkOutput;

public abstract class Gander<T, N> implements IContextProvider,
		INetworkProvider, NetworkInputListener<T> {

	/** An Android Context. */
	protected Context mContext;

	/** A delegate to make callbacks on. */
	protected GanderDelegate<T> mDelegate;

	/** Network output interface. */
	protected NetworkOutput<T> mNetworkOutput;

	/** Network adapter interface for (de)serialization. */
	protected NetworkAdapter<T, N> mNetworkAdapter;

	/** A geographic location helper. */
	protected LocationHelper mLocationHelper;

	/**
	 * TODO: How is data passed to/from here? - content values + adapter - typed
	 * class - typed class with VertexFrame mirror
	 */

	public Gander(Context context, GanderDelegate<T> delegate,
			NetworkOutput<T> networkOutput, NetworkAdapter<T, N> networkAdapter) {
		mContext = context;
		mDelegate = delegate;
		mNetworkOutput = networkOutput;
		mNetworkAdapter = networkAdapter;
		mLocationHelper = LocationHelper.getInstance(context);
	}

	public void sendData(T data) {
		mNetworkOutput.sendData(data);
	}

	/* IContextProvider interface implementation */

	@Override
	public Geoshape getLocation() {
		Location loc = mLocationHelper.getLocation();
		return Geoshape.point(loc.getLatitude(), loc.getLongitude());
	}

	@Override
	public long getTimestamp() {
		return System.currentTimeMillis();
	}

	@Override
	public String getDomain() {
		return Build.SERIAL;
	}

	/* INetworkProvider interface implementation */

	@Override
	public void send(Datum datum, Iterator<SpaceTimePosition> trajectory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void send(Map<Datum, Iterator<SpaceTimePosition>> data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void send(Graph graph) {
		// TODO Auto-generated method stub
	}

}
