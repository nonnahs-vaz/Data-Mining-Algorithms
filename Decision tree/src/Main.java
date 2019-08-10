import java.io.*;
import dataset.DataSet;
import decision_tree.DecisionTree;
class Main {
    public static void main(String[] args) {
        String weatherDatasetPath = "/home/shannon/Documents/practical/DM/Decision tree/datasets/weather.txt";
        String shapesDatasetPath = "/home/shannon/Documents/practical/DM/Decision tree/datasets/shapes.txt";
        System.out.println("Running decision tree algorithm on shapes data set");
        runID3(shapesDatasetPath);
        System.out.println("Running decision tree algorithm on weather data set");
        runID3(weatherDatasetPath);
    }

    static void runID3(String filePath) {
        DataSet dataSet;
        try {
            File dataFile = new File(filePath);
            dataSet = new DataSet(dataFile);
            System.out.println("data set:");
            dataSet.displayRecords();
            DecisionTree t = new DecisionTree();
            t.constructTree(dataSet);
            t.displayTree();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
