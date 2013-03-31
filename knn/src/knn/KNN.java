/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author KhecKhec
 */
public class KNN {

    public static int K = 1;
    public static int METRIC = 2;
    public static int N = 0;
    public static int D = 0;
    public static int C = 0;

    public static Point[] dataTrain;
    public static Point[] dataTest;
    public static Point[] Centroid;
    /*
     * reading data inputs from file
     */
    public static Point[] dataInput(String file) {
        try {
            Scanner sc = new Scanner(new File(file));
            
            N = sc.nextInt();
            D = sc.nextInt();
            C = sc.nextInt();
            Point[] data = new Point[N];
            
            /* Read N data points of D dimensions */
            for(int i = 0; i < N; i++) {
                data[i] = new Point(D);
                for(int j = 0; j < D; j++) {
                    data[i].setValue(j, sc.nextDouble());
                }
                /* get the class label of the data point */
                data[i].setLabel(sc.nextDouble());
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        } 
    } 
    /* Write the data output to files using given format */
    public static void dataOutput(String fileName, Point[] dataTest, double[] predictedLabel){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < dataTest.length; i++) {
                double[] data = dataTest[i].getData();
                for (int j = 0; j < D; j++) {
                    out.write(data[j] + " ");
                }
                out.write(predictedLabel[i] + "");
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Error write result to file");
        }
    }
    /* 
     * Main program
     */
    public static void main(String[] args) {

        String trainDataFile = "";
        String testDataFile = "";
        String predictionFile = "";
        long startTime = System.nanoTime();
        // Parse command line options
        try {
            if (args.length < 2) {
                System.out.println("Insufficient command line options");				
                return;
            }

            trainDataFile = args[0];
            testDataFile = args[1];
            for (int i = 2; i < args.length; i++) {
                /*  If K is not avaiable, the default value is 1 */     
                if (args[i].equals("-k")) {
                    K = Integer.parseInt(args[i+1]);
                    i++;
                /*  
                 * if Metric is not avaialbe, the default value is 2 which
                 * is Euclidean distance
                 */
                } else if (args[i].equals("-d")) {
                    METRIC = Integer.parseInt(args[i+1]);
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing command line options");
            return;
        }

        /* Read training data set */
        dataTrain = dataInput(trainDataFile);
        
        /* Read test data set */
        dataTest = dataInput(testDataFile);

        /* Get number of class labels */
        List<Double> labelList = new ArrayList<Double>();		
        for (int i = 0; i < dataTrain.length; i++) {
            if (!labelList.contains(Double.valueOf(dataTrain[i].getLabel())))
                labelList.add(Double.valueOf(dataTrain[i].getLabel()));
        }
        C = labelList.size();

        double[] predictedLabel = new double[dataTest.length];

        for (int i = 0; i<dataTest.length;i++){  
            double[] distance = new double[dataTrain.length];
            
            /* Compute the distance to all data points in the training set */
            for (int j = 0; j<dataTrain.length;j++){
                switch(METRIC){
                    case 0: 
                        /*      METRIC TYPE = 0: COSINE SIMILARITY      */
                        distance[j]= dataTest[i].getCosDistance1(dataTrain[j]);
                        break;
                    case 1: 
                        /*      METRIC TYPE = 1: L1 DISTANCE            */
                        distance[j]= dataTest[i].getL1Distance(dataTrain[j]);
                        break;
                    case 2: 
                        /*      METRIC TYPE = 2: L2 DISTANCE            */
                        distance[j]= dataTest[i].getEucliDistance(dataTrain[j]); 
                        break;
                }
            }

            //get index of K nearesr neighbor 
            int[] neighbor = new int[K];
            neighbor = getKNearestNeighbors(K,distance);

            //get class label of K nearest neighbor
            double[] neighborLabel = new double[K];
            double[] neighborDistance = new double[K];

            for(int j = 0; j<K;j++){
                neighborLabel[j] = dataTrain[neighbor[j]].getLabel();
                neighborDistance[j] = distance[neighbor[j]];
            }

            //assign predicted label to test point 
            predictedLabel[i] = identifyLabel(neighborLabel,neighborDistance);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        //compute accuracy of the predicted label
        double accuracy = computeAccuracy(dataTest,predictedLabel);

        /* Print to output file */
        predictionFile = testDataFile.concat("_prediction.txt");
        dataOutput(predictionFile, dataTest, predictedLabel);
        
        /* Print out the accuracy and the execution time */
        System.out.println("Accuracy = " + (accuracy*100) + "%");;
        System.out.println("Timecost = " + ((double)duration/1000000000) + "seconds");
    }
    /* 
     * Get K nearest neighbors given the number K and the distance array
     */
    public static int[] getKNearestNeighbors(int k, double[] d){
        int[] res = new int[k];
        boolean[] isMin = new boolean[d.length];
        double min=-1;

        for (int j=0;j<d.length;j++){
            isMin[j]=false;
        }
        /* 
         * We run the following algorithm K times:
         * Each time we find the nearest point which has not been 
         * chosen before as the next neighbors
         */
        for (int i = 0; i<k;i++){
            boolean assignMin = false;
            for (int j=0;j<d.length;j++){        

                if (isMin[j]==false && assignMin==false) {
                    min = d[j];
                    assignMin=true;
                    res[i]=j;
                }

                if (d[j]< min && isMin[j]==false){
                    min = d[j];
                    res[i]=j;
                }
            }
            isMin[res[i]]=true;
        }
        return res;
    }
    /* 
     * Determine the class label of the test data point from its K 
     * nearest neighbors
     */
    public static double identifyLabel(double[] neighborLabel,double[] neighborDistance){
        double predictedLabel = -1;
        Label[] labelList = new Label[C];
        int countlabel = 0;
        boolean addMem = false;

        /*
         * Perform voting from neighbors, each time we increase the value
         * of class(x) by an amount of 1/d(x)^2
         */
        for(int i = 0; i<neighborLabel.length;i++){
            addMem = false;
            for (int j=0; j<countlabel;j++){
                if (neighborLabel[i] == labelList[j].getLabel()){
                    labelList[j].addMember(1/(neighborDistance[j]*neighborDistance[j]));                    
                    addMem = true;
                    break;
                }
            }
            if (!addMem){
                labelList[countlabel] = new Label(neighborLabel[i],1/(neighborDistance[i]*neighborDistance[i])); 
                countlabel++;
            }
        }
        /*
         * The class with highest weight is assigned to our test data point
         */
        double maxWeight = 0;
        int index = -1;
        for(int i = 0; i < countlabel; i++){
            if(labelList[i].getWeight()> maxWeight){
                maxWeight = labelList[i].getWeight();
                index = i;
            }
        }
        predictedLabel = labelList[index].getLabel();
        return predictedLabel;
    }
    /* 
     * Compute the accuracy of a given prediction, the accuracy is computed 
     * as n_correct_Answer/n_total_answer
     */
    public static double computeAccuracy(Point[] trueLabel, double[] predictedLabel){
        double acc = 0;
        int truecount=0;
        for(int i=0; i < trueLabel.length;i++){
            if (Double.valueOf(trueLabel[i].getLabel()).compareTo(Double.valueOf(predictedLabel[i])) == 0)
                truecount++;
        }
        acc = (double)truecount/trueLabel.length;
        return acc;

    }
}
