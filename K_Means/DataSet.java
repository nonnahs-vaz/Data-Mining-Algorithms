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

    public void removeAll() {
        records.clear();
    }

    public Record getRecord(int index) {
        return records.get(index);
    }

    public void updateAttributeNames(ArrayList <String> values) {
        attributeNames = values;
    }

    public void updateAttributeNames(String [] values) {
        Collections.addAll(attributeNames,values);
    }

    public ArrayList <String> getAttributeNames() {
        return attributeNames;
    }

    public void addRecord(String line) {
        Record record = new Record(line.split(","));
        records.add(record);
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public int size() {
        return records.size();
    }

    public int getAttributeCount() {   //return number of non-class attributes
        return attributeNames.size() - 1;
    }

    public Record getMean() {   //compute mean of all records
        double mean[] = new double[getAttributeCount()];
        for(int i = 0; i < records.size(); i++) {   //for every record in the data set
            Record r = records.get(i);
            for(int j = 0; j < mean.length; j++) {  //for each attribute of the record
                mean[j] = mean[j] + Double.parseDouble(r.getAttributeValue(j));
            }
        }
        int totalRecords = records.size();
        for(int i = 0; i < mean.length; i++) {
            mean[i] = mean[i]/totalRecords;  //compute mean
        }
        String values[] = new String [mean.length + 1];
        for(int i = 0; i < values.length - 1; i++) {
            values[i] = "" + mean[i];
        }
        values[values.length - 1] = "?";    //set class label of centroid to '?''
        return (new Record(values));
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
