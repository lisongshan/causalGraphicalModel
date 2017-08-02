package net.ipsoft.ipcenter.iprca.causality.learning.gies;

import net.ipsoft.ipcenter.iprca.causality.graph.model.AdjImpl.ChainGraph;
import net.ipsoft.ipcenter.iprca.causality.graph.model.AdjImpl.Vertex;
import net.ipsoft.ipcenter.iprca.causality.graph.model.Node;
import net.ipsoft.ipcenter.iprca.causality.graph.model.intervention.InterventionFamily;
import net.ipsoft.ipcenter.iprca.causality.learning.gies.operation.Operation;
import net.ipsoft.ipcenter.iprca.causality.learning.gies.operation.TurningEssential;
import net.ipsoft.ipcenter.iprca.causality.learning.gies.operation.TurningNotEssential;
import net.ipsoft.ipcenter.iprca.causality.scoreFunction.ScoreFunction;

/**
 * Created by sli on 10/28/15.
 *
 * Algorithm 3
 */
public class StepTurning extends Step {

    public StepTurning(ChainGraph essentialGraph, ScoreFunction scoreFunction, InterventionFamily interventionFamily) {
        super(essentialGraph, scoreFunction, interventionFamily);
    }



    public void perturbEssentialGraph() throws Exception {

        Operation optimalOperation = null;

        for(Node vNode : essentialGraph.getNodes()){
            Vertex v = essentialGraph.getVertexByNumber(vNode.getNumber());
            for(Node u : v.getNeighbors() ){

                Operation tunningNotIessential = new TurningNotEssential(interventionFamily, essentialGraph, u, vNode, scoreFunction).propose();
                if(optimalOperation == null || tunningNotIessential.getMaxScoreChange() > optimalOperation.getMaxScoreChange()){
                    optimalOperation = tunningNotIessential;
                }
            }


            for(Node u : v.getChildren() ){
                Operation tunningIessential = new TurningEssential(interventionFamily, essentialGraph, u, v.getNode(), scoreFunction).propose();
                if(optimalOperation == null || tunningIessential.getMaxScoreChange() > optimalOperation.getMaxScoreChange()){
                    optimalOperation = tunningIessential;
                }
            }
        }

        if(optimalOperation != null && optimalOperation.getMaxScoreChange() > SCORE_DIFF_THRESHOLD){
            optimalOperation.commit();
        }

    }
}
