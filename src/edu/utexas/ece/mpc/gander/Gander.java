package edu.utexas.ece.mpc.gander;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.tinkerpop.blueprints.Graph;

import edu.utexas.ece.mpc.gander.adapters.IGraphAdapter;
import edu.utexas.ece.mpc.gander.adapters.INetworkAdapter;
import edu.utexas.ece.mpc.gander.location.LocationHelper;
import edu.utexas.ece.mpc.gander.network.NetworkInputListener;
import edu.utexas.ece.mpc.gander.network.NetworkOutput;
import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.INetworkProvider;
import edu.utexas.ece.mpc.stdata.SpatiotemporalDatabase;
import edu.utexas.ece.mpc.stdata.factories.VertexFrameFactory;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.Datum;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePosition;

public abstract class Gander implements IContextProvider, INetworkProvider,
		NetworkInputListener {

	/** An Android Context. */
	protected Context mContext;

	/** A delegate to make callbacks on. */
	protected GanderDelegate mDelegate;

	/** Network output interface. */
	protected NetworkOutput mNetworkOutput;

	/** Map of network adapters for (de)serialization of network I/O. */
	protected Map<Class, INetworkAdapter> mNetworkAdapters;

	/** Map of graph adapters for (de)serialization of graph I/O. */
	protected Map<Class, IGraphAdapter> mGraphAdapters;

	/** A geographic location helper. */
	protected LocationHelper mLocationHelper;

	/**
	 * The spatiotemporal graph database used for facilitating remote
	 * device-to-device searches.
	 */
	protected SpatiotemporalDatabase mSTDB;

	// TODO context update timertask (how often do we update our notion of space
	// and time)

	public Gander(Context context, GanderDelegate delegate,
			NetworkOutput networkOutput) {
		mContext = context;
		mDelegate = delegate;

		mNetworkOutput = networkOutput;
		mNetworkOutput.setNetworkInputListener(this);
		mNetworkOutput.setNetworkAdapters(mNetworkAdapters);

		mLocationHelper = LocationHelper.getInstance(context);

		initializeSTDatabase();
	}

	public Gander(Context context, GanderDelegate delegate,
			NetworkOutput networkOutput, INetworkAdapter networkAdapter,
			IGraphAdapter graphAdapter) {
		this(context, delegate, networkOutput);

		if (networkAdapter != null)
			addNetworkAdapter(networkAdapter);

		if (graphAdapter != null)
			addGraphAdapter(graphAdapter);
	}

	public Gander(Context context, GanderDelegate delegate,
			NetworkOutput networkOutput, INetworkAdapter[] networkAdapters,
			IGraphAdapter[] graphAdapters) {
		this(context, delegate, networkOutput);

		if (networkAdapters != null) {
			for (INetworkAdapter adapter : networkAdapters)
				addNetworkAdapter(adapter);
		}

		if (graphAdapters != null) {
			for (IGraphAdapter adapter : graphAdapters)
				addGraphAdapter(adapter);
		}

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
	public void addNetworkAdapter(INetworkAdapter adapter) {
		if (mNetworkAdapters == null)
			mNetworkAdapters = new HashMap<Class, INetworkAdapter>();

		// insert a reference to the adapter for each adapter type so it may be
		// looked up by both
		mNetworkAdapters.put(adapter.getApplicationDataType(), adapter);
		mNetworkAdapters.put(adapter.getNetworkDataType(), adapter);
	}

	public void addGraphAdapter(IGraphAdapter adapter) {
		if (mGraphAdapters == null)
			mGraphAdapters = new HashMap<Class, IGraphAdapter>();

		// insert a reference to the adapter for each adapter type so it may be
		// looked up by both
		mGraphAdapters.put(adapter.getApplicationDataType(), adapter);
		mGraphAdapters.put(adapter.getGraphDataType(), adapter);

		// add the adapter's factory to the graph database
		mSTDB.addVertexFrameFactory(adapter.getGraphDataType(),
				(VertexFrameFactory) adapter);
	}

	/**
	 * Sends a typed piece of application data over the network.
	 * 
	 * @param type
	 *            the type of the application data.
	 * @param data
	 *            a piece of application data to send.
	 */
	public <T> void sendData(Class<T> type, T data) {
		mNetworkOutput.sendData(type, data);
	}

	/**
	 * Sends a typed piece of application data over the network and attaches any
	 * number of unregistered rules to the data.
	 * 
	 * @param type
	 *            the type of the application data.
	 * @param data
	 *            a piece of application data to send.
	 * @param rules
	 *            unregistered rules to associate with the data.
	 */
	public <T> void sendData(Class<T> type, T data, Rule... rules) {
		// send the data
		mNetworkOutput.sendData(type, data);

		// store the data with its associated rule
		storeData(type, data, rules);
	}

	/**
	 * Stores a typed piece of application data in the graph database.
	 * 
	 * @param type
	 *            the type of the application data.
	 * @param data
	 *            a piece of application data to store.
	 */
	public <T, D> void storeData(Class<T> type, T data) {
		// lookup the graph adapter associated with this data type
		IGraphAdapter adapter = mGraphAdapters.get(type);

		// create a graph instance of the data
		adapter.serialize(data);
	}

	/**
	 * Stores a typed piece of application data in the graph database and
	 * attaches any number of unregistered rules to the data.
	 * 
	 * @param type
	 *            the type of the application data.
	 * @param data
	 *            a piece of application data to store.
	 * @param rules
	 *            unregistered rules to associate with the data.
	 */
	public <T> void storeData(Class<T> type, T data, Rule... rules) {
		// lookup the graph adapter associated with this data type
		IGraphAdapter adapter = mGraphAdapters.get(type);

		// create a graph instance of the data with the associated rule
		adapter.serialize(data, rules);
	}

	/**
	 * Registers a new rule with the graph database.
	 * 
	 * @param rules
	 *            the rule to register.
	 */
	public void registerRule(Rule rule) {
		mSTDB.getRuleRegistry().registerRule(rule);
	}

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
	public <D extends Datum> void send(Class<D> type, D datum) {
		// lookup the graph adapter associated with this graph data type
		IGraphAdapter adapter = mGraphAdapters.get(type);

		// deserialize the graph data into application data
		Object data = adapter.deserialize(datum);

		// TODO acquire spatiotemporal metadata?
		
		// send the data over the network
		sendData(adapter.getApplicationDataType(), data);
	}

	@Override
	public <D extends Datum> void send(Class<D> type, D datum, boolean attachTrajectory) {
		// TODO Auto-generated method stub

	}

	@Override
	public <D extends Datum> void send(Class<D> type, D datum,
			Iterator<SpaceTimePosition> trajectory) {
		// TODO Auto-generated method stub

	}

	@Override
	public <D extends Datum> void send(Class<D> type, Map<D, Iterator<SpaceTimePosition>> data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void send(Graph graph) {
		// TODO Auto-generated method stub
	}

}
