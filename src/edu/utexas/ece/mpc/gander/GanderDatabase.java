package edu.utexas.ece.mpc.gander;

import java.util.Iterator;

import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePositionVertex;

public interface GanderDatabase {

	/**
	 * Returns any spatiotemporal metadata associated with a piece of
	 * application data if that data exists in the local gander database.
	 * 
	 * @param type
	 *            the type of the application data.
	 * @param data
	 *            a piece of application data.
	 * @return an iterator over the data's spatiotemporal metadata if it exists,
	 *         null otherwise.
	 */
	public <T, D extends DatumVertex> Iterator<SpaceTimePositionVertex> getTrajectory(
			Class<T> type, T data);
}
