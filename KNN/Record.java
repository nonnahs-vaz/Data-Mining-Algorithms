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
        return attributes.size() - 1;
    }

    public String getAttributeValue(int columnNumber) {
        return attributes.get(columnNumber);   //return value of specific attribute
    }

    public void display() {
        for(String value : attributes) {
            System.out.print(value + "\t");
        }
        System.out.println();
    }

    public Boolean isNumeric() {
        try {
            for(int i = 0; i < attributes.size() - 1; i++) {    //for every value in attributes
                Double.parseDouble(attributes.get(i));  //try to convert value to double
            }
            return true;    //if all values were successfully converted return true
        }
        catch(NumberFormatException e) {
            return false;   //one value was a string
        }
    }
}
