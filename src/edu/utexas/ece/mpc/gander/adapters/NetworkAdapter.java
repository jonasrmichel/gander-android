package edu.utexas.ece.mpc.gander.adapters;


public interface NetworkAdapter<T, N> {

	public N serialize(T src);

	public T deserialize(N data);

}
