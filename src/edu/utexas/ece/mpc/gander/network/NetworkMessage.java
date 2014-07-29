package edu.utexas.ece.mpc.gander.network;

import edu.utexas.ece.mpc.gander.graph.SpaceTimePosition;
import edu.utexas.ece.mpc.stdata.rules.Rule;

public class NetworkMessage<T> {

	/** The message payload. */
	protected T mPayload = null;

	/** The spatiotemporal trajectory associated with the payload data. */
	protected SpaceTimePosition[] mTrajectory = null;

	/** The spatiotemporal rules associated with the payload data. */
	protected Rule[] mRules = null;

	public NetworkMessage() {
		// no args constructor
	}

	public NetworkMessage(T payload, SpaceTimePosition[] trajectory,
			Rule... rules) {
		mPayload = payload;
		mTrajectory = trajectory;
		mRules = rules;
	}

	public T getPayload() {
		return mPayload;
	}

	public void setPayload(T payload) {
		mPayload = payload;
	}

	public SpaceTimePosition[] getTrajectory() {
		return mTrajectory;
	}

	public void setTrajectory(SpaceTimePosition[] trajectory) {
		mTrajectory = trajectory;
	}

	public Rule[] getRules() {
		return mRules;
	}

	public void setRules(Rule[] rules) {
		mRules = rules;
	}
}
