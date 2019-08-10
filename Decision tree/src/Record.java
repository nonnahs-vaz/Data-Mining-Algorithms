package dataset;
import java.util.Collections;
import java.util.ArrayList;
public class Record {
    private ArrayList <String> attributes;  //all attribute values of record
    private String classLabel;  //class label assigned to record

    public Record(String values[]) {
        attributes = new ArrayList <String>();
        Collections.addAll(attributes,values);
        classLabel = attributes.get(attributes.size() - 1); //making last value in array as class label
    }

    public String getClassLabel() {
        return classLabel;  //return class label of record
    }

    public int size() {
        return attributes.size();
    }

    public String getAttributeValue(int columnNumber) {
        return attributes.get(columnNumber);   //return value of specific attribute
    }

    public void removeAttributeValue(int columnNumber) {
        attributes.remove(columnNumber);    //remove specific column of recored
    }

    public Record getCopy() {
        String attr[] = new String [this.attributes.size()];
        int i = 0;
        for(String str : this.attributes) { //loop to copy all attribute values into a string array
            attr[i] = str;
            i++;
        }
        return (new Record(attr));  //create a new record with the copied values
    }

    public void display() {
        for(String value : attributes) {
            System.out.print(value + "\t");
        }
        System.out.println();
    }
}
