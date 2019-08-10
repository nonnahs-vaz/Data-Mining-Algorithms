package decision_tree;
import dataset.DataSet;
import java.util.HashMap;
class Node {
    //if node is a leaf
    private Boolean leafNode;   //indicates whether node is a leaf or not
    private HashMap <String,Integer> predictions; //set of all classes and their counts at this node
    //if node is not a leaf
    private String splittingAttributeName;  //attribute on which split was done
    private int splittingAttributeIndex;
    private HashMap <String,Node> branches; //branches going out from this node for a particular attribute value

    Node(Boolean isLeaf, DataSet D) {   //node is a leaf node
        leafNode = isLeaf;
        predictions = D.getClassCounts();    //compute class predictions for current node
    }

    Node(DataSet D, int splitAttrIndex, String splitAttrName, HashMap <String,Node> br) { //node is a decision node
        leafNode = false;
        predictions = D.getClassCounts();
        splittingAttributeName = splitAttrName;
        splittingAttributeIndex = splitAttrIndex;
        branches = br;  //get all links from current node to children
    }

    Boolean isLeaf() {
        return leafNode;
    }

    String getSplittingAttributeName() {
        return splittingAttributeName;
    }

    HashMap <String,Node> getSplits() {
        return branches;
    }

    void displayPredictions(String spacing) {  //display predictions of current node
        System.out.println(spacing + "Predictions:");
        for(String classLabel : predictions.keySet()) {
            System.out.println(spacing + classLabel + " : " + predictions.get(classLabel));
        }
    }
}
