import java.util.*;
import java.io.*;
class KNN {
    public static void main(String[] args) {
        String record1 = "5.0,?",record2 = "C,T,H,?";
        Record testRecord1 = new Record(record1.split(","));
        Record testRecord2 = new Record(record2.split(","));
        DataSet first,second;
        try {
            first = new DataSet(new File("./datasets/first.txt"));
            second = new DataSet(new File("./datasets/second.txt"));
        }
        catch(IOException e) {
            System.out.println(e);
            return;
        }

        System.out.println("Data set is:");
        first.displayRecords();
        System.out.println("\nTest record is:");
        testRecord1.display();
        runKNN(first,testRecord1,1);
        runKNN(first,testRecord1,3);
        runKNN(first,testRecord1,5);
        runKNN(first,testRecord1,9);
        System.out.println("\n");
        System.out.println("Data set is:");
        second.displayRecords();
        System.out.println("\nTest record is:");
        testRecord2.display();
        runKNN(second,testRecord2,2);
    }

    static void runKNN(DataSet D, Record testRecord, int k) {
        System.out.println();
        System.out.println("Running KNN on data set for k = "+k);
        System.out.println("class label predicted is: "+classify(D,testRecord,k));
    }

    public static String classify(DataSet D, Record testRecord, int k) { //classify a record using the record in data set
        ArrayList <Neighbor> neighbors = getKNearestNeighbors(D,testRecord,k);   //get k nearest neighbors from test record
        return getClass(neighbors); //get the class from the neighbors
    }

    static ArrayList <Neighbor> getKNearestNeighbors(DataSet D, Record testRecord, int k) {
        ArrayList <Neighbor> neighbors = new ArrayList <Neighbor>();
        for(int i = 0; i < D.size(); i++) {    //for every record in dataset
            Record r1 = D.getRecord(i);
            Neighbor n = computeDistance(r1,testRecord);    //get distance between test record and dataset record
            Boolean neighborInserted = false;
            for(int j = 0; j < neighbors.size(); j++) { //put neighbor in list in increasing order of distance
                if(neighbors.get(j).getDistance() > n.getDistance()) {  //if existing neighbor has greater distance than newly found neighbor
                    neighbors.add(j,n); //put new neighbor before existing neighbor
                    neighborInserted = true;
                    break;
                }
            }
            if(neighborInserted == false) { //if neighbor could not be inserted in between
                neighbors.add(n);   //insert neighbor at end
            }
        }
        ArrayList <Neighbor> nearestNeighbors = new ArrayList <Neighbor>();
        for(int i = 0; i < k; i++) {    //add first k neighbors into a new list
            nearestNeighbors.add(neighbors.get(i));
        }
        return nearestNeighbors; //return k nearest neighbors
    }

    static Neighbor computeDistance(Record r1, Record r2) {    //get distance between two records
        double distance;
        if(r1.isNumeric()) {
            distance = computeNumericDistance(r1,r2);
        }
        else {
            distance = computeNonNumericDistance(r1,r2);
        }
        return (new Neighbor(r1,distance));
    }

    static double computeNumericDistance(Record r1, Record r2) {   //compute euclidean distance between two records
        double distance = 0,x1,x2;
        for(int i = 0; i < r1.size(); i++) {
            x1 = Double.parseDouble(r1.getAttributeValue(i));
            x2 = Double.parseDouble(r2.getAttributeValue(i));
            distance += (x1 - x2) * (x1 - x2);
        }
        return Math.sqrt(distance);
    }

    static double computeNonNumericDistance(Record r1, Record r2) {
        double distance = 0;
        String x1,x2;
        for(int i = 0; i < r1.size(); i++) {
            x1 = r1.getAttributeValue(i);
            x2 = r2.getAttributeValue(i);
            if(x1.equals(x2) == false)  //if attribute values dont match
                distance += 1; //increase distance;
        }
        return distance;
    }

    static String getClass(ArrayList <Neighbor> neighbors) {
        HashMap <String,Integer> classCounts = new HashMap <String,Integer>();
        for(Neighbor n : neighbors) {
            String classLabel = n.getClassLabel();
            if(classCounts.containsKey(classLabel)) {   //class label is already seen
                classCounts.put(classLabel,classCounts.get(classLabel) + 1); //increment class count
            }
            else {
                classCounts.put(classLabel,1);  //make entry for new class label
            }
        }
        String majorityClass = "";
        int maxCount = 0, count;
        Boolean conflict = false;   //denotes max count shared by two or more classes
        for(String classLabel : classCounts.keySet()) {
            count = classCounts.get(classLabel);
            if(count > maxCount) {    //if class label has higher count
                majorityClass = classLabel;
                maxCount = count;
                conflict = false;
            }
            else if(count == maxCount) {    //if a class with same count as max count is found
                conflict = true;    //there is a conflict in making class decision
            }
        }
        if(conflict == true)
            return resolveConflict(neighbors);
        return majorityClass;
    }

    static String resolveConflict(ArrayList <Neighbor> neighbors) {
        HashMap <String,Double> inverseDistances = new HashMap <String,Double>();
        Double inverseDistanceWeight,weight;
        for(Neighbor n : neighbors) {
            String classLabel = n.getClassLabel();
            weight = n.getDistance();
            inverseDistanceWeight = 1 / (weight * weight);
            if(inverseDistances.containsKey(classLabel)) {   //class label is already seen
                inverseDistances.put(classLabel,inverseDistances.get(classLabel) + inverseDistanceWeight); //sum up the inverse distance weights
            }
            else {
                inverseDistances.put(classLabel,inverseDistanceWeight);  //make entry for new class label
            }
        }
        String majorityClass = "";
        Double maxDistance = 0.0;
        for(String classLabel : inverseDistances.keySet()) {
            inverseDistanceWeight = inverseDistances.get(classLabel);
            if(inverseDistanceWeight > maxDistance) {    //if class label has higher inverse distance weight
                majorityClass = classLabel;
                maxDistance = inverseDistanceWeight;
            }
        }
        return majorityClass;
    }
}

class Neighbor {
    Record record;
    Double distance;

    Neighbor(Record r, double dist) {
        this.record = r;
        distance = dist;
    }

    Double getDistance() {
        return distance;
    }

    String getClassLabel() {
        return record.getClassLabel();
    }
}
