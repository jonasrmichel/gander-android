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

import edu.utexas.ece.mpc.gander.location.LocationHelper;

public class Gander implements IContextProvider, INetworkProvider {

	/** A delegate to make callbacks on. */
	private Delegate mDelegate;

	public static interface Delegate {

		/**
		 * Called to obtain an Android Context.
		 * 
		 * @return the Gander middleware's Android Context.
		 */
		public Context getContext();
	}

	/** A geographic location helper. */
	private LocationHelper mLocationHelper;

	public Gander(Delegate delegate) {
		mDelegate = delegate;
		mLocationHelper = LocationHelper.getInstance(delegate.getContext());
	}

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

}
