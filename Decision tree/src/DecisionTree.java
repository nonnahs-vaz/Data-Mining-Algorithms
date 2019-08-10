package decision_tree;
import dataset.DataSet;
import dataset.Record;
import java.util.*;
public class DecisionTree {
    private Node root;

    public void constructTree(DataSet D) {
        root = buildTree(D);
    }

    private Node buildTree(DataSet D) { //build decision tree for dataset and update root node of tree
        int splittingAttribute = findBestSplit(D);
        if(splittingAttribute == -1)  //node cannot be split any further
            return (new Node(true,D));    //create leaf node
        DataSet subset;
        HashSet <String> uniqueValues = D.getDistinctAttributeValues(splittingAttribute);   //get unique values for the attribute
        HashMap <String,Node> branches = new HashMap <String,Node>();   //branches to each subset based on attribute value
        for(String value : uniqueValues) {  //for every unique value for attribute
            subset = D.getSubset(D.getAttributeName(splittingAttribute),value); //get a subset of the original dataset
            branches.put(value,buildTree(subset));  //grow the tree for the subset
        }
        return (new Node(D,splittingAttribute,D.getAttributeName(splittingAttribute),branches));   //return decision node
    }

    private static int findBestSplit(DataSet D) {  //get column number of best attribute to split on
        int splittingAttribute = -1;    //-1 if cannot split any further
        int attributeCount = D.getAttributeCount(); //get number of non class attributes
        double currentEntropy = D.getEntropy(); //get entropy of data set
        double bestGain = 0;
        double gain;
        for(int attributeIndex = 0; attributeIndex < attributeCount; attributeIndex++) {    //check each attribute for a split
            ArrayList <DataSet> subsets = getSubsets(D, attributeIndex);    //get all partitions for current value of attribute
            gain = getInfoGain(subsets,currentEntropy); //get gain of the split
            if(gain > bestGain) {   //if a better split was found
                splittingAttribute = attributeIndex;    //update the splitting attribute
                bestGain = gain;    //update new best gain
            }
        }
        return splittingAttribute;
    }

    private static ArrayList <DataSet> getSubsets(DataSet D, int attributeIndex) {
        ArrayList <DataSet> subsets = new ArrayList <DataSet>();    //subsets of parent dataset
        HashSet <String> uniqueValues = D.getDistinctAttributeValues(attributeIndex);   //get unique values for the attribute
        for(String value : uniqueValues) {  //for every unique value for attribute
            subsets.add(D.getSubset(D.getAttributeName(attributeIndex),value)); //get a subset of the original dataset
        }
        return subsets;
    }

    private static double getInfoGain(ArrayList <DataSet> subsets, double parentEntropy) { //compute information gain for the split
        double weightedEntropy = 0;
        double totalRecords = 0;
        for(DataSet S : subsets) {  //for every split of dataset
            weightedEntropy += S.size() * S.getEntropy();   //multiply weight of node to its entropy
            totalRecords += S.size();   //get total number of records
        }
        weightedEntropy /= totalRecords;    //compute final weighted entropy
        return (parentEntropy - weightedEntropy);   //return gain
    }

    public void displayTree() {
        System.out.println("\nDecision tree is: ");
        displayTree(root,"");
        System.out.println();
    }

    private void displayTree(Node explorer, String spacing) {
        if(explorer.isLeaf()) {
            explorer.displayPredictions(spacing);
            return;
        }
        String splittingAttr = explorer.getSplittingAttributeName();
        HashMap <String,Node> splits = explorer.getSplits();
        for(String splittingAttrValue : splits.keySet()) {
            System.out.println(spacing + splittingAttr + " == " + splittingAttrValue);
            displayTree(splits.get(splittingAttrValue),spacing + "  ");
        }
    }
}
