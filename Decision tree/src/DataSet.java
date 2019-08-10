package dataset;
import java.util.*;
import java.io.*;
public class DataSet {
    private ArrayList <Record> records; //records
    private ArrayList <String> attributeNames;  //attribute names

    public DataSet() {
        records = new ArrayList <Record>();
        attributeNames = new ArrayList <String>();
    }

    public DataSet(File data) throws IOException {
        records = new ArrayList <Record>();
        attributeNames = new ArrayList <String>();
        BufferedReader in = new BufferedReader(new FileReader(data));
        String line = in.readLine();
        updateAttributeNames(line.split(","));
        while((line = in.readLine()) != null) {
            addRecord(line);
        }
        in.close();
    }

    public Boolean isEmpty() {
        return records.isEmpty();
    }

    public String getAttributeName(int attributeIndex) {    //return name of attribute for a specific column
        return attributeNames.get(attributeIndex);
    }

    public void updateAttributeNames(String [] values) {
        Collections.addAll(attributeNames,values);
    }

    public void removeAttribute(String attributeName) { //remove a specific attribute from the dataset
        int attributeIndex = attributeNames.indexOf(attributeName); //get index of the attribute
        for(Record r : records) {
            r.removeAttributeValue(attributeIndex);    //remove attribute value from every record
        }
        attributeNames.remove(attributeIndex);  //remove attribute from the list of attribute names
    }

    public void addRecord(String line) {
        Record record = new Record(line.split(","));
        records.add(record);
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public DataSet getSubset(String attributeName, String attributeValue) { //get one partition of dataset for a specific attribute and corresponding value
        DataSet replica = new DataSet();
        String attrNames[] = new String [this.attributeNames.size()];
        int i = 0;
        for(String str : this.attributeNames) { //copy all names of attributes into a new array
            attrNames[i] = str;
            i++;
        }
        replica.updateAttributeNames(attrNames);    //copy attribute names into replica dataset
        int attributeIndex = attributeNames.indexOf(attributeName); //get index of the attribute
        for(Record r : this.records) {  //for each record
            if(attributeValue.equals(r.getAttributeValue(attributeIndex))) { //if attribute value matches
                Record c = r.getCopy(); //get record for specific value of specific attribute
                replica.addRecord(c);   //copy record into replica dataset
            }
        }
        replica.removeAttribute(attributeName); //remove the attribute from the set
        return replica;
    }

    public int size() {
        return records.size();
    }

    public int getAttributeCount() {   //return number of non-class attributes
        return attributeNames.size() - 1;
    }

    public double getEntropy() {    //computes entropy of the dataset
        double totalRecords = records.size();
        double entropy = 0;
        HashMap <String,Integer> classCounts = getClassCounts();    //get count of all classes
        for(String classLabel : classCounts.keySet()) {
            double p = classCounts.get(classLabel)/totalRecords;    //compute probability of class label in dataset
            entropy -= pLog2p(p);
        }
        return entropy;
    }

    private static double pLog2p(double p) {    //compute log base 2 of a number
        if(p == 0) return 0.0;
        return (p * Math.log(p)/Math.log(2));
    }

    public HashMap <String,Integer> getClassCounts() {   //get class and corresponding count
        HashMap <String,Integer> classCounts = new HashMap <String,Integer>();
        for(Record r : records) {
            String classLabel = r.getClassLabel();
            if(classCounts.containsKey(classLabel)) {   //class label is already seen
                classCounts.put(classLabel,classCounts.get(classLabel) + 1); //increment class count
            }
            else {
                classCounts.put(classLabel,1);  //make entry for new class label
            }
        }
        return classCounts;
    }

    public HashSet <String> getDistinctAttributeValues(int columnNumber) {  //get all unique values in a specific column
        HashSet <String> values = new HashSet <String>();
        for(Record r : records) {
            values.add(r.getAttributeValue(columnNumber));
        }
        return values;
    }

    public void displayRecords() {
        for(String attrName : attributeNames) {
            System.out.print(attrName+"\t");
        }
        System.out.println();
        for(Record record : records) {
            record.display();
        }
    }
}
