package net.graphical.model.causality.model;

import net.graphical.model.causality.scoreFunction.NodesConfiguration;
import net.graphical.model.causality.graph.model.Node;
import net.graphical.model.causality.interventionData.NodeProb;

/**
 * Created by sli on 12/4/15.
 */
public abstract class ConditionalDistribution {
    protected CpdTable cpdTable = new CpdTable();


    public NodeProb getProb(Node node, NodesConfiguration parentsConfiguration) throws Exception {

        NodeProb nodeProb = cpdTable.getTimedNodeProb(parentsConfiguration, node);


        if(nodeProb == null){
            nodeProb = doGetNodeProb(node, parentsConfiguration);
            cpdTable.add(parentsConfiguration, nodeProb);
        }
        return nodeProb;
    }

    public CpdTable getCpdTable() {
        return cpdTable;
    }

    public void setCpdTable(CpdTable cpdTable) {
        this.cpdTable = cpdTable;
    }

    protected abstract NodeProb doGetNodeProb(Node node, NodesConfiguration parentsConfiguration) throws Exception;


}
