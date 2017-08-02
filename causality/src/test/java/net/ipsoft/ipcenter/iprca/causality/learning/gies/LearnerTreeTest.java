package net.ipsoft.ipcenter.iprca.causality.learning.gies;

import net.ipsoft.ipcenter.iprca.causality.graph.algorithm.graphTypeChecking.DagChecker;
import net.ipsoft.ipcenter.iprca.causality.graph.algorithm.transformer.EssentialGraphGenerator;
import net.ipsoft.ipcenter.iprca.causality.graph.model.AdjImpl.ChainGraph;
import net.ipsoft.ipcenter.iprca.causality.graph.model.AdjImpl.Dag;
import net.ipsoft.ipcenter.iprca.causality.graph.model.AdjImpl.Vertex;
import net.ipsoft.ipcenter.iprca.causality.graph.model.Edge;
import net.ipsoft.ipcenter.iprca.causality.graph.model.EdgeType;
import net.ipsoft.ipcenter.iprca.causality.graph.model.Node;
import net.ipsoft.ipcenter.iprca.causality.graph.model.intervention.Intervention;
import net.ipsoft.ipcenter.iprca.causality.interventionData.DataSet;
import net.ipsoft.ipcenter.iprca.causality.interventionData.NodeProb;
import net.ipsoft.ipcenter.iprca.causality.interventionData.dataGenerator.CausalModel;
import net.ipsoft.ipcenter.iprca.causality.utils.DataSetStore;
import net.ipsoft.ipcenter.iprca.causality.interventionData.dataGenerator.OneCpd;
import net.ipsoft.ipcenter.iprca.causality.scoreFunction.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by sli on 12/2/15.
 */
public class LearnerTreeTest {

    @Test
    public void testAutoGeneratedObservationDataSet_7Nodes () throws Exception {

        Intervention intervention = new Intervention(Arrays.asList());

        List<Intervention> interventions = Arrays.asList(intervention);


        CausalModel causalModel = new CausalModel(getTree(), new OneCpd());
        DataSet dataSet = causalModel.generateDataSet(4000, interventions);

        BicScoreFunction scoreFunction = new BicScoreFunction(dataSet);


        for(Vertex v : causalModel.getDag().getVertexes()){
            Node node = v.getNode();
            LocalComponent localComponent = new LocalComponent(node, v.getParents());
            for(NodesConfiguration parentConfig : localComponent.getNodesConfigurations()){
                NodeProb np = scoreFunction.getEmpiricalProb(node, parentConfig);
                NodeProb np_defined = causalModel.getCpd().getProb(node, parentConfig);
                assertEquals(np.getProb()[0], np_defined.getProb()[0], 0.05);
                assertEquals(np.getProb()[1], np_defined.getProb()[1], 0.05);
            }
        }
    }

    @Test
    public void testLearner_7Variables_autogen_inv0_100000_BIC() throws Exception {

        Intervention intervention = new Intervention(Arrays.asList());

        List<Intervention> interventions = Arrays.asList(intervention);

        Dag dag = getTree();
        ChainGraph dagCopy = dag.deepCopy();

        CausalModel causalModel = new CausalModel(dag, new OneCpd());
        DataSet dataSet = causalModel.generateDataSet(100000, interventions);

        ScoreFunction scoreFunction = new BicScoreFunction(dataSet);

        double score_exact = scoreFunction.score(dag);
        Learner learner = new Learner(dataSet.getColumnNodes(), scoreFunction, dataSet.getInterventionFamily());
        ChainGraph ed = learner.learnEssentialGraph();

        EssentialGraphGenerator generator = new EssentialGraphGenerator(getTree());
        ChainGraph oldEd = generator.toEssentialGraph();

        System.out.println("score_exact " + score_exact);
        System.out.println (dagCopy.makeEssential(dataSet.getInterventionFamily()).toString());

        double score_learned = scoreFunction.score(ed);

        System.out.println("score_learned " + score_learned);
        System.out.println (ed.toString());

        assertEquals(score_exact, score_learned, 0.001);
    }


    @Test
    public void testLearner_7Variables_autogen_inv0_100000_BDeu() throws Exception {

        Intervention intervention = new Intervention(Arrays.asList());

        List<Intervention> interventions = Arrays.asList(intervention);

        Dag dag = getTree();
        ChainGraph dagCopy = dag.deepCopy();

        CausalModel causalModel = new CausalModel(dag, new OneCpd());
        DataSet dataSet = causalModel.generateDataSet(100000, interventions);

        ScoreFunction scoreFunction = new BDeuScoreFunction( dataSet, 1.0, 2);//new BicScoreFunction(dataSet);

        double score_exact = scoreFunction.score(dag);
        Learner learner = new Learner(dataSet.getColumnNodes(), scoreFunction, dataSet.getInterventionFamily());
        ChainGraph ed = learner.learnEssentialGraph();

        EssentialGraphGenerator generator = new EssentialGraphGenerator(getTree());
        ChainGraph oldEd = generator.toEssentialGraph();

        System.out.println("score_exact " + score_exact);
        System.out.println (dagCopy.makeEssential(dataSet.getInterventionFamily()).toString());

        double score_learned = scoreFunction.score(ed);

        System.out.println("score_learned " + score_learned);
        System.out.println (ed.toString());

        assertEquals(score_exact, score_learned, 0.001);
    }


    @Test
    @Ignore
    public void testLearner_7Variables_autogen_inv0_100000_BDeu_21() throws Exception {

        Intervention intervention = new Intervention(Arrays.asList());

        List<Intervention> interventions = Arrays.asList(intervention);

        Dag dag = getTree();
        ChainGraph dagCopy = dag.deepCopy();

        CausalModel causalModel = new CausalModel(dag, new OneCpd());
        DataSet dataSet = causalModel.generateDataSet(100000, interventions);

        ScoreFunction scoreFunction = new BDeuScoreFunction( dataSet, 0.2, 1);//new BicScoreFunction(dataSet);

        double score_exact = scoreFunction.score(dag);
        Learner learner = new Learner(dataSet.getColumnNodes(), scoreFunction, dataSet.getInterventionFamily());
        ChainGraph ed = learner.learnEssentialGraph();

        EssentialGraphGenerator generator = new EssentialGraphGenerator(getTree());
        ChainGraph oldEd = generator.toEssentialGraph();

        System.out.println("score_exact " + score_exact);
        System.out.println (dagCopy.makeEssential(dataSet.getInterventionFamily()).toString());

        double score_learned = scoreFunction.score(ed);

        System.out.println("score_learned " + score_learned);
        System.out.println (ed.toString());

        assertEquals(score_exact, score_learned, 0.001);
    }


    @Test
    public void testLearner_tree_fromFile_inv0_4000_BDeu() throws Exception {


        DataSetStore store = new DataSetStore("bug_ds.txt");

        Dag dag = getTree();
        ChainGraph dagCopy = dag.deepCopy();

        DataSet dataSet = store.load();

        ScoreFunction scoreFunction = new BDeuScoreFunction( dataSet,0.2, 1);//new BicScoreFunction(dataSet);

        double score_exact = scoreFunction.score(dag);
        Learner learner = new Learner(dataSet.getColumnNodes(), scoreFunction, dataSet.getInterventionFamily());
        ChainGraph ed = learner.learnEssentialGraph();

        EssentialGraphGenerator generator = new EssentialGraphGenerator(getTree());
        ChainGraph oldEd = generator.toEssentialGraph();

        System.out.println("score_exact " + score_exact);
        System.out.println (dagCopy.makeEssential(dataSet.getInterventionFamily()).toString());

        double score_learned = scoreFunction.score(ed);

        System.out.println("score_learned " + score_learned);
        System.out.println (ed.toString());

        assertEquals(score_exact, score_learned, 0.01);
    }

    @Test
    public void testLearner_tree_fromFile_inv0_4000_Bic() throws Exception {


        DataSetStore store = new DataSetStore("bug_ds.txt");

        Dag dag = getTree();
        ChainGraph dagCopy = dag.deepCopy();

        DataSet dataSet = store.load();

        ScoreFunction scoreFunction = new BicScoreFunction( dataSet);//new BicScoreFunction(dataSet);

        double score_exact = scoreFunction.score(dag);
        Learner learner = new Learner(dataSet.getColumnNodes(), scoreFunction, dataSet.getInterventionFamily());
        ChainGraph ed = learner.learnEssentialGraph();

        EssentialGraphGenerator generator = new EssentialGraphGenerator(getTree());
        ChainGraph oldEd = generator.toEssentialGraph();

        System.out.println("score_exact " + score_exact);
        System.out.println (dagCopy.makeEssential(dataSet.getInterventionFamily()).toString());

        double score_learned = scoreFunction.score(ed);

        System.out.println("score_learned " + score_learned);
        System.out.println (ed.toString());

        assertEquals(score_exact >= score_learned, true);
    }

    @Test
    public void testLearner_tree_fromFile_inv0_4000_MLE() throws Exception {


        DataSetStore store = new DataSetStore("bug_ds.txt");

        Dag dag = getTree();
        ChainGraph dagCopy = dag.deepCopy();

        DataSet dataSet = store.load();

        ScoreFunction scoreFunction = new MleScoreFunction(dataSet);

        double score_exact = scoreFunction.score(dag);
        Learner learner = new Learner(dataSet.getColumnNodes(), scoreFunction, dataSet.getInterventionFamily());
        ChainGraph ed = learner.learnEssentialGraph();

        EssentialGraphGenerator generator = new EssentialGraphGenerator(getTree());
        ChainGraph oldEd = generator.toEssentialGraph();

        System.out.println("score_exact " + score_exact);
        System.out.println (dagCopy.makeEssential(dataSet.getInterventionFamily()).toString());

        double score_learned = scoreFunction.score(ed);

        System.out.println("score_learned " + score_learned);
        System.out.println (ed.toString());

        assertEquals(ed.noOfUndirectEdge(), 21);
    }

    private Dag getTree(){

        int noOfNodes = 7;
        List<Node> nodes = new ArrayList<>();
        for(int i = 1; i <= noOfNodes; i++){
            Node node = new Node(i);
            node.addLevel("0","1");
            nodes.add(node);
        }

        List<Edge> edges = Arrays.asList(
                new Edge(1,2, EdgeType.DIRECTED_MINUS)
                ,new Edge(2,3, EdgeType.DIRECTED_MINUS), new Edge(2,7, EdgeType.DIRECTED_PLUS)
                ,new Edge(3,4, EdgeType.DIRECTED_PLUS)
                ,new Edge(4,5, EdgeType.DIRECTED_PLUS), new Edge(4,6, EdgeType.DIRECTED_PLUS)
        );

        Dag dag = new Dag(nodes, edges);
        DagChecker checker = new DagChecker(dag);

        assertEquals(checker.isTrue(), true);
        return  dag;
    }

}
