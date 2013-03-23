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
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author KhecKhec
 */
public class KNN_14 {

    public static int K = 1;
    public static int METRIC = 2;
    public static int N = 0;
    public static int D = 0;
    public static int C = 0;
    public static Point[] dataTrain;
    public static Point[] dataTest;
    public static Point[] Centroid;

    public static Point[] dataInput(String file) {
        try {
            Scanner sc = new Scanner(new File(file));
            N = sc.nextInt();
            D = sc.nextInt();
            C = sc.nextInt();
            Point[] data = new Point[N];
            for (int i = 0; i < N; i++) {
                data[i] = new Point(D);
                for (int j = 0; j < D; j++) {
                    data[i].setValue(j, sc.nextDouble());
                }
                data[i].setLabel(sc.nextDouble());
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }

    public static void main(String[] args) {

        String trainDataFile = "";
        String testDataFile = "";
        String predictionFile = "";
        long startTime = System.nanoTime();

        // Parse command line options
        try {
            if (args.length < 3) {
                System.out.println("Insufficient command line options");
                return;
            }

            trainDataFile = args[0];
            testDataFile = args[1];
            for (int i = 2; i < args.length; i++) {
                if (args[i].equals("-k")) {
                    K = Integer.parseInt(args[i + 1]);
                    i++;
                } else if (args[i].equals("-d")) {
                    METRIC = Integer.parseInt(args[i + 1]);
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing command line options");
            return;
        }

        dataTrain = dataInput(trainDataFile);
        dataTest = dataInput(testDataFile);


        // Get number of class labels
        List<Double> labelList = new ArrayList<Double>();
        for (int i = 0; i < dataTrain.length; i++) {
            if (!labelList.contains(Double.valueOf(dataTrain[i].getLabel()))) {
                labelList.add(Double.valueOf(dataTrain[i].getLabel()));
            }
        }
        C = labelList.size();

        double[] allLabel = new double[C];
        for (int i = 0; i < C; i++) {
            allLabel[i] = labelList.get(i);
        }
        Point[] sample = sampleData(dataTrain, allLabel);

        double[] weight = new double[C];
        for (int i = 0; i < C; i++) {
            weight[i] = 0;
        }
        weight = findOptimumWeight(sample, 0.7, K, METRIC);
        double[] predictedLabel = new double[dataTest.length];

        for (int i = 0; i < dataTest.length; i++) {
            double[] distance = new double[dataTrain.length];
            for (int j = 0; j < dataTrain.length; j++) {

                switch (METRIC) {
                    case 0:
                        distance[j] = getWeightedCosDistance(dataTrain[j], dataTest[i], weight);
                        break;
                    case 1:
                        distance[j] = getWeightedL1Distance(dataTrain[j], dataTest[i], weight);
                        break;
                    case 2:
                        distance[j] = getWeightedEuclideDistance(dataTrain[j], dataTest[i], weight);
                        break;
                }

            }

            //get index of K nearesr neighbor 
            int[] neighbor = new int[K];
            neighbor = getMaxIndex(K, distance);

            //get label of K nearest neighbor
            double[] neighborLabel = new double[K];
            double[] neighborDistance = new double[K];

            for (int j = 0; j < K; j++) {
                neighborLabel[j] = dataTrain[neighbor[j]].getLabel();
                neighborDistance[j] = distance[neighbor[j]];
            }

            //assign predicted label to test point 
            predictedLabel[i] = identifyLabel(neighborLabel, neighborDistance);
        }


        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        //compute accuracy of the predicted label
        double accuracy = computeAcc(dataTest, predictedLabel);


        //print output file
        predictionFile = testDataFile.concat("_prediction.txt");
        dataOutput(predictionFile, dataTest, predictedLabel);

        System.out.println("Accuracy = " + (accuracy * 100) + "%");;
        System.out.println("Timecost = " + ((double) duration / 1000000000) + "seconds");
    }

    public static int[] getMinIndex(int k, double[] d) {
        int[] res = new int[k];
        boolean[] isMin = new boolean[d.length];
        double min = -1;

        for (int j = 0; j < d.length; j++) {
            isMin[j] = false;
        }

        for (int i = 0; i < k; i++) {

            boolean assignMin = false;

            for (int j = 0; j < d.length; j++) {

                if (isMin[j] == false && assignMin == false) {
                    min = d[j];
                    assignMin = true;
                    res[i] = j;
                }

                if (d[j] < min && isMin[j] == false) {
                    min = d[j];
                    res[i] = j;
                }
            }

            isMin[res[i]] = true;
        }
        return res;
    }

    public static int[] getMaxIndex(int k, double[] d) {
        int[] res = new int[k];
        boolean[] isMax = new boolean[d.length];
        double max = -1;

        for (int j = 0; j < d.length; j++) {
            isMax[j] = false;
        }

        for (int i = 0; i < k; i++) {

            boolean assignMin = false;

            for (int j = 0; j < d.length; j++) {

                if (isMax[j] == false && assignMin == false) {
                    max = d[j];
                    assignMin = true;
                    res[i] = j;
                }

                if (d[j] > max && isMax[j] == false) {
                    max = d[j];
                    res[i] = j;
                }
            }

            isMax[res[i]] = true;
        }
        return res;
    }

    public static double identifyLabel(double[] klabel, double[] kdistances) {

        int k = klabel.length;
        double max = 0;
        int maxIndex = 0;

        //compute sum of similarity for each classes of label
        for (int i = 0; i < k - 1; i++) {
            if (kdistances[i] == -1) {
                continue;
            }
            for (int j = i + 1; j < k; j++) {
                if (kdistances[j] == -1) {
                    continue;
                }
                if (klabel[i] == klabel[j]) {
                    kdistances[i] += kdistances[j];
                    kdistances[j] = -1;
                }
            }
        }

        //find the class with the most sum of similarity
        for (int i = 0; i < k; i++) {
            if (kdistances[i] > max) {
                max = kdistances[i];
                maxIndex = i;
            }
        }
        return klabel[maxIndex];
    }

    public static double computeAcc(Point[] trueLabel, double[] predictedLabel) {
        double acc = 0;
        int truecount = 0;
        for (int i = 0; i < trueLabel.length; i++) {
            if (Double.valueOf(trueLabel[i].getLabel()).compareTo(Double.valueOf(predictedLabel[i])) == 0) {
                truecount++;
            }
        }
        acc = (double) truecount / trueLabel.length;
        return acc;

    }

    public static void dataOutput(String fileName, Point[] dataTest, double[] predictedLabel) {
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

    public static double getWeightedCosDistance(Point a, Point b, double[] weight) {
        double[] temp = a.getData();
        double[] temp1 = new double[a.getData().length];

        for (int i = 0; i < temp.length; i++) {
            temp1[i] = temp[i] * weight[i];
        }
        Point c = new Point(temp1);

        double[] temp2 = b.getData();
        double[] temp3 = new double[b.getData().length];

        for (int i = 0; i < temp2.length; i++) {
            temp3[i] = temp2[i] * weight[i];
        }
        Point d = new Point(temp3);
        return c.getCosDistance(d);

    }

    public static double getWeightedEuclideDistance(Point a, Point b, double[] weight) {
        double[] temp = a.getData();
        double[] temp1 = new double[a.getData().length];

        for (int i = 0; i < temp.length; i++) {
            temp1[i] = temp[i] * weight[i];
        }
        Point c = new Point(temp1);

        double[] temp2 = b.getData();
        double[] temp3 = new double[b.getData().length];

        for (int i = 0; i < temp2.length; i++) {
            temp3[i] = temp2[i] * weight[i];
        }
        Point d = new Point(temp3);
        return 1/(c.getEucliDistance(d) * c.getEucliDistance(d));

    }

    public static double getWeightedL1Distance(Point a, Point b, double[] weight) {
        double[] temp = a.getData();
        double[] temp1 = new double[a.getData().length];

        for (int i = 0; i < temp.length; i++) {
            temp1[i] = temp[i] * weight[i];
        }
        Point c = new Point(temp1);

        double[] temp2 = b.getData();
        double[] temp3 = new double[b.getData().length];

        for (int i = 0; i < temp2.length; i++) {
            temp3[i] = temp2[i] * weight[i];
        }
        Point d = new Point(temp3);
        return 1/(c.getL1Distance(d)*c.getL1Distance(d));

    }

    public static boolean correctlyClassified(Point a, Point[] neighbours, double[] weight, double percentage, int Metric) {
        double classSimilarity = 0;
        double otherSimilarity = 0;
        double label = a.getLabel();
        for (int i = 0; i < neighbours.length; i++) {
            if (neighbours[i].getLabel() == label) {
                if(Metric == 0)
                    classSimilarity += getWeightedCosDistance(a, neighbours[i], weight);
                else if(Metric == 1)
                    classSimilarity += getWeightedL1Distance(a, neighbours[i], weight);
                else
                    classSimilarity += getWeightedEuclideDistance(a, neighbours[i], weight);
                   
            } else {
                if(Metric == 0)
                    otherSimilarity += getWeightedCosDistance(a, neighbours[i], weight);
                else if(Metric == 1)
                    otherSimilarity += getWeightedL1Distance(a, neighbours[i], weight);
                else
                    otherSimilarity += getWeightedEuclideDistance(a, neighbours[i], weight);
            }
        }
        if (classSimilarity > percentage * (classSimilarity + otherSimilarity)) {
            return true;
        } else {
            return false;
        }
    }

    public static int objectiveFunction(Point[] allpoints, double[] weight, double percentage, int k, int Metric) {
        int n = allpoints.length;
        int correctCount = 0;

        for (int i = 0; i < n; i++) {

            double[] distances = new double[n - 1];
            int count = 0;

            //calculate all weighted cosine distance
            for (int j = 0; j < n && j != i; j++) {
                if(Metric == 0)
                    distances[count] = getWeightedCosDistance(allpoints[i], allpoints[j], weight);
                else if(Metric == 1)
                    distances[count] = getWeightedL1Distance(allpoints[i], allpoints[j], weight);
                else
                    distances[count] = getWeightedEuclideDistance(allpoints[i], allpoints[j], weight);
                count++;
            }

            //extract k nearest neighbours from all weighted cosine distance
            int[] temp = getMaxIndex(k, distances);
            Point[] knearest = new Point[k];
            for (int t = 0; t < k; t++) {
                knearest[t] = allpoints[temp[t]];
            }

            //check if allpoints[i] is correctly classified
            if (correctlyClassified(allpoints[i], knearest, weight, percentage, Metric)) {
                correctCount++;
            }

        }
        return correctCount;
    }

    public static double[] findOptimumWeight(Point[] train, double percentage, int k, int Metric) {
        int attrNumber = train[0].getData().length;
        double[] tune = {0.2, 0.5, 0.8, 1.5, 2, 4};
        double[] weight = new double[attrNumber];

        //initialize the weight
        for (int i = 0; i < attrNumber; i++) {
            weight[i] = 1;
        }

        for (int i = 0; i < attrNumber; i++) {
            int initObjective = objectiveFunction(train, weight, percentage, k, Metric);

            double initWeight = weight[i];
            double[] subObjective = new double[tune.length];
            double maxObjective = 0;
            int maxObjectiveIndex = 0;

            //tune the weight to see if it can be improved:
            //replace the weight with each tune value and compute the objective
            //function for that value
            for (int j = 0; j < tune.length; j++) {
                weight[i] = initWeight * tune[j];
                subObjective[j] = objectiveFunction(train, weight, percentage, k, Metric);
            }

            //check if after tunning the objective value can be improved
            for (int j = 0; j < tune.length; j++) {
                if (subObjective[j] > maxObjective) {
                    maxObjective = subObjective[j];
                    maxObjectiveIndex = j;
                }
            }
            if (maxObjective > initObjective) {
                weight[i] = initWeight * tune[maxObjectiveIndex];
            } else {
                weight[i] = initWeight;
            }

        }
        return weight;
    }

    public static Point[] sampleData(Point[] train, double[] label) {
        int l = label.length;
        int t = train.length;
        ArrayList<Point> sample = new ArrayList<Point>();
        ArrayList[] data = new ArrayList[l];
        int[] classCount = new int[l];
        double samplingFactor = 0;
        int max = 0;

        for (int i = 0; i < l; i++) {
            data[i] = new ArrayList<Point>();
            classCount[i] = 0;
        }

        //classify all data into classes
        for (int i = 0; i < t; i++) {
            for (int j = 0; j < l; j++) {
                if (train[i].getLabel() == label[j]) {
                    data[j].add(train[i]);
                    classCount[j]++;
                }
            }
        }

        //find the sampling factor:
        for (int i = 0; i < l; i++) {
            if (classCount[i] > max) {
                max = classCount[i];
            }
        }

        //if the class with the most members has only < 100 data point -> sample = 1
        if (max > 100) {
            samplingFactor = max / 100;
        } else {
            samplingFactor = 1;
        }

        //get the number of member from each class after sampling
        for (int i = 0; i < l; i++) {
            if (classCount[i] / samplingFactor == 0) {
                continue;
            } else {
                classCount[i] = (int) ((double) classCount[i] / samplingFactor);
            }
        }

        //sample the data
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < classCount[i]; j++) {
                sample.add((Point) data[i].get(j));
            }
        }

        Point[] returnArray = new Point[sample.size()];
        for (int i = 0; i < sample.size(); i++) {
            returnArray[i] = sample.get(i);
        }
        return returnArray;
    }
}
