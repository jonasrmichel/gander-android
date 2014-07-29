package edu.utexas.ece.mpc.gander.adapters;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.SpatiotemporalDatabase;
import edu.utexas.ece.mpc.stdata.factories.DatumFactory;
import edu.utexas.ece.mpc.stdata.factories.ISpaceTimePositionFactory;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;

public abstract class GraphAdapter<T, D extends DatumVertex> extends
		DatumFactory<D> implements IGraphAdapter<T, D> {

	public GraphAdapter(Class<D> type) {
		super(type);
	}

	public GraphAdapter(Class<D> type, TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry,
			IContextProvider contextProvider,
			ISpaceTimePositionFactory stpFactory) {
		super(type, baseGraph, framedGraph, ruleRegistry, contextProvider,
				stpFactory);
		// TODO Auto-generated constructor stub
	}

	/**
	 * A serialization helper that should configure the properties of a
	 * graph-resident instance of this adapter's graph data type to reflect
	 * those of the provided application data object.
	 * 
	 * @param appData
	 *            an application data object whose properties to use.
	 * @param graphData
	 *            a graph-resident datum derivative whose properties to
	 *            configure.
	 */
	protected abstract void configureGraphInstance(T appData, D graphData);

	/**
	 * Returns an iterator over all of the vertices in the graph database that
	 * are framed as this adapter's graph data type.
	 * 
	 * @return an iterator over the vertices framed as this adapter's graph data
	 *         type.
	 */
	public Iterable<D> getFramedVertices() {
		Iterable<Vertex> vertices = baseGraph.getVertices(
				SpatiotemporalDatabase.FRAMED_CLASS_KEY, getGraphDataType());

		Iterable<D> framedVertices = framedGraph.frameVertices(vertices,
				getGraphDataType());

		return framedVertices;
	}
}
