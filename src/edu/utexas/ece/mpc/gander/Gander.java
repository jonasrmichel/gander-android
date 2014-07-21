package edu.utexas.ece.mpc.gander;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import stdata.IContextProvider;
import stdata.INetworkProvider;
import stdata.datamodel.SpatiotemporalDatabase;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.geo.Geoshape;
import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.gremlin.Tokens.T;

import edu.utexas.ece.mpc.gander.adapters.NetworkAdapter;
import edu.utexas.ece.mpc.gander.location.LocationHelper;
import edu.utexas.ece.mpc.gander.network.NetworkInputListener;
import edu.utexas.ece.mpc.gander.network.NetworkOutput;

public abstract class Gander implements IContextProvider, INetworkProvider,
		NetworkInputListener {

	/** An Android Context. */
	protected Context mContext;

	/** A delegate to make callbacks on. */
	protected GanderDelegate mDelegate;

	/** Network output interface. */
	protected NetworkOutput mNetworkOutput;

	/** Map of network adapters for (de)serialization of network I/O. */
	protected Map<Class, NetworkAdapter> mNetworkAdapters;

	/** A geographic location helper. */
	protected LocationHelper mLocationHelper;

	/**
	 * The spatiotemporal graph database used for facilitating remote
	 * device-to-device searches.
	 */
	protected SpatiotemporalDatabase mSTDB;

	public Gander(Context context, GanderDelegate delegate,
			NetworkOutput networkOutput, NetworkAdapter... networkAdapters) {
		mContext = context;
		mDelegate = delegate;

		// configure the network output interface
		mNetworkOutput = networkOutput;
		mNetworkOutput.setNetworkInputListener(this);
		mNetworkOutput.setNetworkAdapters(mNetworkAdapters);

		mLocationHelper = LocationHelper.getInstance(context);

		if (networkAdapters != null) {
			for (NetworkAdapter adapter : networkAdapters)
				addNetworkAdapter(adapter);
		}

		initializeSTDatabase();
	}

	/**
	 * Initializes the implementation-specific spatiotemporal graph database.
	 */
	protected abstract void initializeSTDatabase();

	/**
	 * Adds a network adapter.
	 * 
	 * @param adapter
	 *            a network adapter.
	 */
	public void addNetworkAdapter(NetworkAdapter adapter) {
		if (mNetworkAdapters == null)
			mNetworkAdapters = new HashMap<Class, NetworkAdapter>();

		// insert a reference to the adapter for each adapter type so it may be
		// looked up by both
		mNetworkAdapters.put(adapter.getApplicationDataType(), adapter);
		mNetworkAdapters.put(adapter.getNetworkDataType(), adapter);
	}

	/**
	 * Sends a typed piece of application data over the network.
	 * 
	 * @param type
	 *            the type of the application data.
	 * @param data
	 *            a piece of application data
	 */
	public <T> void sendData(Class<T> type, T data) {
		mNetworkOutput.sendData(type, data);
	}

	// TODO public <F extends VertexFrame> void addVertexFactory(Class<F> type,
	// VertexFactory<F> factory)
	// TODO public <T> void storeData(Class<T> type, T data)

	/* NetworkInputListener interface implementation */

	@Override
	public <T> void receivedData(String source, T data) {
		mDelegate.receivedData(source, data);
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
