package net.ipsoft.ipcenter.iprca.causality.interventionData;

import net.ipsoft.ipcenter.iprca.causality.graph.model.Node;
import net.ipsoft.ipcenter.iprca.causality.graph.model.intervention.Intervention;
import net.ipsoft.ipcenter.iprca.causality.utils.DataSetStore;
import net.ipsoft.ipcenter.iprca.causality.scoreFunction.BicScoreFunction;
import net.ipsoft.ipcenter.iprca.causality.scoreFunction.NodesConfiguration;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by sli on 12/2/15.
 */
public class ScoreFunctionTest {

    @Test
    public void testManualGeneratedDataSet_2Nodes () throws Exception {

        DataSet dataSet = generationDataSet_twoBinaryVariables(5000);

        for(Node node : dataSet.getColumnNodes()){
            node.addLevel("0");
            node.addLevel("1");
        }

        DataSetStore dataStore = new DataSetStore(new Date().getTime() + "_ds.txt");
        dataStore.save(dataSet);


        BicScoreFunction scoreFunction = new BicScoreFunction(dataSet);
        double s1 = scoreFunction.getLocalScore(dataSet.getColumnNode(1), Arrays.asList(dataSet.getColumnNode(2)));


        Node node1 = dataSet.getColumnNode(1);
        Node node2 = dataSet.getColumnNode(2);


        NodeProb np = scoreFunction.getEmpiricalProb(node1, new NodesConfiguration());
        NodeProb np2_10 = scoreFunction.getEmpiricalProb(node2, new NodesConfiguration(node1, "0"));
        NodeProb np2_11 = scoreFunction.getEmpiricalProb(node2, new NodesConfiguration(node1, "1"));

    }



    /**
     * @throws Exception
     * X1->X2
     * P(X1=0) = p; P(X1=1) = 1-p;
     * P(X2=0|X1=0) = q2,P(X2=1|X1=0) = 1- q2,P(X2=0|X1=1) = r2,P(X2=1|X1=1) = r2,
     * P(X2=0) = p*q2 + (1-p)*r2, P(X2=1) = p(1-q2) + (1-p) *(1-r2);
     */

    public DataSet  generationDataSet_twoBinaryVariables(int count) throws Exception
    {

        List<DataEntry> dataEntries = new ArrayList<>();

        Intervention observation = new Intervention(Arrays.asList());
        Intervention intervention = new Intervention(Arrays.asList(new Node(1)));
        Node x1Node = new Node(1);
        x1Node.addLevel("0","1");

        Node x2Node = new Node(2);
        x2Node.addLevel("0","1");

        List<Node> columnNodes = Arrays.asList(x1Node, x2Node);

        double p=0.5, q2=0.2, r2=0.8;

        //generate observation data
        for(int i = 0; i < count; i++){
            DataEntry e1 = new DataEntry(observation, 2);
            SecureRandom rd = new SecureRandom();

            String x1="0";
            String x2="0";
            if(rd.nextDouble() > p){
                x1 = "1";
            }

            if("0".equals(x1) && rd.nextDouble() > q2){
                x2 = "1";
            }

            if("1".equals(x1) && rd.nextDouble() > r2){
                x2 = "1";
            }

            e1.addData(1, x1);
            e1.addData(2, x2);
            dataEntries.add(e1);
        }

        //generate interventional data
        // x1="0";
        for(int i = 0; i < count/2; i++){
            DataEntry e1 = new DataEntry(intervention, 2);
            SecureRandom rd = new SecureRandom();

            String x2="0";
            if(rd.nextDouble() > q2){
                x2 = "1";
            }

            e1.addData(1, "0");
            e1.addData(2, x2);
            dataEntries.add(e1);
        }

        // x1="1";
        for(int i = 0; i < count/2; i++){
            DataEntry e1 = new DataEntry(intervention,2);
            SecureRandom rd = new SecureRandom();

            String x2="0";
            if(rd.nextDouble() > r2){
                x2 = "1";
            }

            e1.addData(1, "1");
            e1.addData(2, x2);
            dataEntries.add(e1);
        }

        return new DataSet(columnNodes, dataEntries);

    }


}
