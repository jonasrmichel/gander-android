package edu.utexas.ece.mpc.gander.graph;

import java.util.ArrayList;
import java.util.Iterator;

import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePositionVertex;

public class SpaceTimePosition {

	protected double mLatitude;
	protected double mLongitude;
	protected long mTimestamp;
	protected String mDomain;

	public SpaceTimePosition() {
		// no args constructor
	}

	public SpaceTimePosition(double latitude, double longitude, long timestamp,
			String domain) {
		mLatitude = latitude;
		mLongitude = longitude;
		mTimestamp = timestamp;
		mDomain = domain;
	}

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	public long getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(long mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public String getDomain() {
		return mDomain;
	}

	public void setDomain(String mDomain) {
		this.mDomain = mDomain;
	}

	/**
	 * Deserializes a graph instance of a space time position.
	 * 
	 * @param position
	 *            a graph instance of a space time position.
	 * @return a raw instance of a space time position.
	 */
	public static SpaceTimePosition deserialize(SpaceTimePositionVertex position) {
		return new SpaceTimePosition(position.getLocation().getPoint()
				.getLatitude(), position.getLocation().getPoint()
				.getLongitude(), position.getTimestamp(), position.getDomain());
	}

	/**
	 * Deserializes the collection of graph instances of space time positions
	 * referenced by the provided iterator.
	 * 
	 * @param trajectory
	 *            an iterator over a collection of space time position graph
	 *            instances.
	 * @return an array of raw space time positions.
	 */
	public static SpaceTimePosition[] deserialize(
			Iterator<SpaceTimePositionVertex> trajectory) {
		ArrayList<SpaceTimePosition> list = new ArrayList<SpaceTimePosition>();

		SpaceTimePositionVertex position;
		while (trajectory.hasNext()) {
			position = trajectory.next();
			list.add(deserialize(position));
		}

		return list.toArray(new SpaceTimePosition[list.size()]);
	}
}
