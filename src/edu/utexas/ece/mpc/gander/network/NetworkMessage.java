package edu.utexas.ece.mpc.gander.network;

import edu.utexas.ece.mpc.stdata.rules.Rule;

public class NetworkMessage<T> {

	/** The message payload. */
	protected T mPayload;

	/** The spatiotemporal rules associated with this message's payload data. */
	protected Rule[] mRules;

	public NetworkMessage() {
		// no args constructor
	}
	
	public NetworkMessage(T payload, Rule... rules) {
		mPayload = payload;
		mRules = rules;
	}

	public T getPayload() {
		return mPayload;
	}
	
	public void setPayload(T payload) {
		mPayload = payload;
	}

	public Rule[] getRules() {
		return mRules;
	}
	
	public void setRules(Rule[] rules) {
		mRules = rules;
	}
}
