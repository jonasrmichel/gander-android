package edu.utexas.ece.mpc.gander.adapters;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.factories.DatumFactory;
import edu.utexas.ece.mpc.stdata.factories.ISpaceTimePositionFactory;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.vertices.Datum;

public abstract class GraphAdapter<T, D extends Datum> extends DatumFactory<D>
		implements IGraphAdapter<T, D> {

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

}
