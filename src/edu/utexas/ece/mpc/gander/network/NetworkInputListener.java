package edu.utexas.ece.mpc.gander.network;

public interface NetworkInputListener<T> {

	public void receivedData(String source, T data);
}
