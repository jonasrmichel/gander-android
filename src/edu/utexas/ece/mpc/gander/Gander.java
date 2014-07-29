package edu.utexas.ece.mpc.gander;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import edu.utexas.ece.mpc.gander.adapters.IGraphAdapter;
import edu.utexas.ece.mpc.gander.adapters.INetworkAdapter;
import edu.utexas.ece.mpc.gander.graph.SpaceTimePosition;
import edu.utexas.ece.mpc.gander.location.LocationHelper;
import edu.utexas.ece.mpc.gander.network.NetworkIO;
import edu.utexas.ece.mpc.gander.network.NetworkInputListener;
import edu.utexas.ece.mpc.gander.network.NetworkMessage;
import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.INetworkProvider;
import edu.utexas.ece.mpc.stdata.SpatiotemporalDatabase;
import edu.utexas.ece.mpc.stdata.factories.VertexFrameFactory;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePositionVertex;

public abstract class Gander implements IContextProvider, INetworkProvider,
		NetworkInputListener, GanderDatabase {

	/** An Android Context. */
	protected Context mContext;

	/** A delegate to make callbacks on. */
	protected GanderDelegate mDelegate;

	/** Network I/O interface. */
	protected NetworkIO mNetworkIO;

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

	/** A timer to fire spatiotemporal contextual update tasks. */
	private Timer mTimer;

	public Gander(Context context, GanderDelegate delegate,
			long updateInterval, NetworkIO networkIO) {
		mContext = context;
		mDelegate = delegate;

		mNetworkIO = networkIO;
		mNetworkIO.setNetworkInputListener(this);
		mNetworkIO.setNetworkAdapters(mNetworkAdapters);

		mLocationHelper = LocationHelper.getInstance(context);

		initializeSTDatabase();

		// schedule periodic spatial temporal contextual updates
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				mSTDB.updateSpatiotemporalContext();
				mSTDB.commit();
			}

		}, updateInterval, updateInterval);
	}

	public Gander(Context context, GanderDelegate delegate,
			long updateInterval, NetworkIO networkOutput,
			INetworkAdapter networkAdapter, IGraphAdapter graphAdapter) {
		this(context, delegate, updateInterval, networkOutput);

		if (networkAdapter != null)
			addNetworkAdapter(networkAdapter);

		if (graphAdapter != null)
			addGraphAdapter(graphAdapter);
	}

	public Gander(Context context, GanderDelegate delegate,
			long updateInterval, NetworkIO networkOutput,
			INetworkAdapter[] networkAdapters, IGraphAdapter[] graphAdapters) {
		this(context, delegate, updateInterval, networkOutput);

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
	 * Safely shuts everything down.
	 */
	public void shutdown() {
		mTimer.cancel();
		mNetworkIO.shutdown();
		mSTDB.shutdown();
	}

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
	 * Sends a typed piece of application data over the network and attaches any
	 * number of rules to the data.
	 * 
	 * @param type
	 *            the type of the application data.
	 * @param data
	 *            a piece of application data to send.
	 * @param attachTrajectory
	 *            if true, any spatiotemporal trajectory associated with this
	 *            piece of data will be attached to the generated outgoing
	 *            {@link NetworkMessage}.
	 * @param rules
	 *            rules to associate with the data.
	 */
	public <T> void sendData(Class<T> type, T data, boolean attachTrajectory,
			Rule... rules) {
		SpaceTimePosition[] trajectory = null;
		if (attachTrajectory)
			trajectory = SpaceTimePosition
					.deserialize(getTrajectory(type, data));

		mNetworkIO.sendData(type, data, trajectory, rules);
	}

	/**
	 * Sends a typed piece of application data over the network and attaches any
	 * number of rules to the data.
	 * 
	 * @param type
	 *            the type of the application data.
	 * @param data
	 *            a piece of application data to send.
	 * @param trajectory
	 *            the application data object's associated spatiotemporal
	 *            metadata.
	 * @param rules
	 *            rules to associate with the data.
	 */
	public <T> void sendData(Class<T> type, T data,
			Iterator<SpaceTimePositionVertex> trajectory, Rule... rules) {
		mNetworkIO.sendData(type, data,
				SpaceTimePosition.deserialize(trajectory), rules);
	}

	/**
	 * Stores a typed piece of application data in the graph database and
	 * attaches any number of unregistered rules to the data.
	 * 
	 * @param type
	 *            the type of the application data.
	 * @param data
	 *            a piece of application data to store.
	 * @param trajectory
	 *            any spatiotemporal metadata associated with the application
	 *            data object.
	 * @param rules
	 *            unregistered rules to associate with the data.
	 */
	public <T> void storeData(Class<T> type, T data,
			SpaceTimePosition[] trajectory, Rule... rules) {
		// lookup the graph adapter associated with this data type
		IGraphAdapter adapter = mGraphAdapters.get(type);

		// create a graph instance of the data with the associated rule
		adapter.serialize(data, trajectory, rules);

		// commit changes
		mSTDB.commit();
	}

	/**
	 * Registers a new rule with the graph database.
	 * 
	 * @param rules
	 *            the rule to register.
	 */
	public void registerRule(Rule rule) {
		mSTDB.getRuleRegistry().registerRule(rule);

		// commit changes
		mSTDB.commit();
	}

	/* NetworkInputListener interface implementation */

	@Override
	public <T> void receivedData(String source, Class<T> type, T data,
			SpaceTimePosition[] trajectory, Rule... rules) {
		// store this data in the graph database
		storeData(type, data, trajectory, rules);

		// alert the delegate
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
	public <D extends DatumVertex> void send(Class<D> type, D datum,
			boolean attachTrajectory, Rule... rules) {
		// lookup the graph adapter associated with this graph data type
		IGraphAdapter adapter = mGraphAdapters.get(type);

		// deserialize the graph data into application data
		Object data = adapter.deserialize(datum);

		// send the data over the network
		sendData(adapter.getApplicationDataType(), data, false, rules);
	}

	@Override
	public <D extends DatumVertex> void send(Class<D> type, D datum,
			Iterator<SpaceTimePositionVertex> trajectory, Rule... rules) {
		// lookup the graph adapter associated with this graph data type
		IGraphAdapter adapter = mGraphAdapters.get(type);

		// deserialize the graph data into application data
		Object data = adapter.deserialize(datum);

		// send the data over the network
		sendData(adapter.getApplicationDataType(), data, trajectory, rules);
	}

	/* GanderDatabase interface implementation */

	@Override
	public <T, D extends DatumVertex> Iterator<SpaceTimePositionVertex> getTrajectory(
			Class<T> type, T data) {
		// lookup the graph adapter associated with this data type
		IGraphAdapter adapter = mGraphAdapters.get(type);

		// find the graph instance of the provided piece of application data
		D datum = (D) adapter.find(data);

		// return the spatiotemporal trajectory
		return adapter.getTrajectory(datum);
	}
}
