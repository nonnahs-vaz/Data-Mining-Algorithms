import java.io.*;
import java.util.*;
class KMeans {
    public static void main(String[] args) {
        ArrayList <Record> initialCentroids = new ArrayList <Record>();
        Record firstCentroid = new Record("5.1,3.5,1.4,0.2,Iris-setosa".split(","));
        Record secondCentroid = new Record("7.0,3.2,4.7,1.4,Iris-versicolor".split(","));
        Record thirdCentroid = new Record("6.3,3.3,6.0,2.5,Iris-virginica".split(","));
        initialCentroids.add(firstCentroid);
        initialCentroids.add(secondCentroid);
        initialCentroids.add(thirdCentroid);
        System.out.println("Initial centroids chosen: ");
        for(Record r : initialCentroids) {
            System.out.println("Centroid:");
            r.display();
            System.out.println();
        }

        DataSet D;
        try {
            D = new DataSet(new File("/home/shannon/Documents/practical/DM/K_Means/datasets/iris_dataset.txt"));
        }
        catch(IOException e) {
            System.out.println(e);
            return;
        }
        int max_iterations = 5;
        System.out.println("Data set is: ");
        D.displayRecords();
        System.out.println("\nRunning K-Means on dataset with max iterations = "+max_iterations);
        ArrayList <Cluster> clusters = getClusters(D,3,initialCentroids,max_iterations);
        System.out.println("Clusters obtained:\n");
        for(Cluster c : clusters) {
            c.display();
            System.out.println();
        }
    }

    static ArrayList <Cluster> getClusters(DataSet D, int k, ArrayList <Record> initialCentroids, int max_iterations) {
        ArrayList <Cluster> clusters = new ArrayList <Cluster>();
        for(int i = 0; i < k; i++) {    //loop to initialize k clusters with k initial centroids
            Cluster c = new Cluster(D.getAttributeNames(),initialCentroids.get(i));
            clusters.add(c);
        }
        for(int i = 0; i < max_iterations; i++) {
            for(int clusterNumber = 0; clusterNumber < k; clusterNumber++) {    //for each cluster in the list of clusters
                clusters.get(clusterNumber).dumpRecords();  //remove all records from the cluster
            }
            for(int recordIndex = 0; recordIndex < D.size(); recordIndex++) {   //for each record in the dataset
                Record r = D.getRecord(recordIndex);
                double leastDistance = 9999,distance;
                int clusterIndex = 0;
                for(int clusterNumber = 0; clusterNumber < k; clusterNumber++) {    //for each cluster in the list of clusters
                    distance = computeDistance(r,clusters.get(clusterNumber).getCentroid()); //compute distance between record and centroid of cluster
                    if(distance < leastDistance) {
                        leastDistance = distance;
                        clusterIndex = clusterNumber;
                    }
                }
                clusters.get(clusterIndex).addRecord(r);    //add record to cluster with least distance
            }
            for(int clusterNumber = 0; clusterNumber < k; clusterNumber++) {    //for each cluster in the list of clusters
                clusters.get(clusterNumber).updateCentroid();
            }
        }
        return clusters;
    }

    static double computeDistance(Record r1, Record r2) {   //compute euclidean distance between two records
        double distance = 0,x1,x2;
        for(int i = 0; i < r1.size() - 1; i++) {
            x1 = Double.parseDouble(r1.getAttributeValue(i));
            x2 = Double.parseDouble(r2.getAttributeValue(i));
            distance += (x1 - x2) * (x1 - x2);
        }
        return Math.sqrt(distance);
    }
}

class Cluster {
    DataSet D;
    Record centroid;

    Cluster(ArrayList <String> attrNames, Record c) {
        D = new DataSet();
        D.updateAttributeNames(attrNames);
        centroid = c;
    }

    void addRecord(Record r) {
        D.addRecord(r);
    }

    Record getCentroid() {
        return centroid;
    }

    void updateCentroid() {
        centroid = D.getMean(); //new centroid is the mean of the dataset
    }

    void dumpRecords() {
        if(D.isEmpty() == false)
            D.removeAll();
    }

    void display() {
        System.out.println("Centroid:");
        centroid.display();
        System.out.println("\nRecords:");
        D.displayRecords();
        System.out.println();
    }
}
