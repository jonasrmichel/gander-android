package edu.utexas.ece.mpc.gander;


public interface GanderDelegate<T> {
	
	public void receivedData(String source, T data);
}
