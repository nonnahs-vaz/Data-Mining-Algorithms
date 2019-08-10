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

    public void updateAttributeNames(String [] values) {
        Collections.addAll(attributeNames,values);
    }

    public void addRecord(String line) {
        Record record = new Record(line.split(","));
        records.add(record);
    }

    public int size() {
        return records.size();
    }

    public int getAttributeCount() {   //return number of non-class attributes
        return attributeNames.size() - 1;
    }

    public Record getRecord(int index) {
        return records.get(index);
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

    public Boolean isNumericDataSet() { //check if dataset is numeric
        return records.get(0).isNumeric();
    }
}
