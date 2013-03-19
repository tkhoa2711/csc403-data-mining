/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kmean;

import java.io.*;
import java.util.*;
/**
 *
 * @author Vu
 */
public class KMean {

    /**
     * @param args the command line arguments
     */
    public static Point[] data;
    public static Point[] Centroid;
    public static double eps = 0;
    /*************************DATA I/O***************************/
    public static void dataInput() {
        try {
            Scanner sc = new Scanner(new File("dna.txt"));
            Const.N = sc.nextInt();
            Const.D = sc.nextInt();
            Const.C = sc.nextInt();
            data = new Point[Const.N];
            System.out.println("nPoint = " + Const.N);
            System.out.println("nDimension = " + Const.D);
            for(int i = 0; i < Const.N; i++) {
                data[i] = new Point(Const.D);
                data[i].label = 0;
                for(int j = 0; j < Const.D; j++) {
                    data[i].setValue(j, sc.nextDouble());
                }
                sc.nextDouble();
            }
        } catch (IOException e) {
            System.out.println("There is no input file");
        }
    }
    
    public static double minsseError;
    public static double getSSEError() {
        double sseError = 0;
        for(int i = 0; i < Const.N; i++) {
            sseError += data[i].getDistance(Centroid[data[i].label]); 
        }
        return sseError;
    }
    public static void dataOutput() {
        for(int i = 0; i < Const.N; i++) {
     //       System.out.println("Class " + i + " = " + data[i].label);
        }
        double sseError = getSSEError();
        minsseError = Math.min(minsseError,sseError);
    //    System.out.println("SSE = " + sseError);
    }
    /********************** Initial Centroids***************************************/
    public static void generateKInitialCentroids() {
        Centroid = new Point[Const.K];
        
        //shuffle the sequence to select randomly K points as Centroid
        int[] Index = new int[Const.N];
        for(int i = 0; i < Const.N; i++) {
            Index[i] = i;
            int j = (int)(Math.random() * i);
            int tmp = Index[i];
            Index[i] = Index[j];
            Index[j] = tmp;
        }
        
        System.out.print("Select points as centroids: ");
        for(int i = 0; i < Const.K; i++) {
            Centroid[i] = new Point(Const.D);
            Centroid[i].setData(data[Index[i]]);
            System.out.print(Index[i] + " ");
        }
        System.out.println();
    }
    
    public static void improvedKInitialCentroids() {
        Centroid = new Point[Const.K];
        
        Point Origin = new Point(Const.D);
        Origin.setZero();
        
        List<DataPoint> datapoints = new ArrayList<DataPoint>();
        for(int i = 0; i < Const.N; i++) {
            datapoints.add(new DataPoint(data[i].getDistance(Origin),i));
        }
        Collections.sort(datapoints);
        int bandwidth = Const.N / Const.K;
        if (Const.N % Const.K != 0) bandwidth++;
        for(int i = 0; i < Const.K; i++) {
            Centroid[i] = new Point(Const.D);
            Centroid[i].setZero();
            int cnt = 0;
            for(int j = i * bandwidth; j < Math.min(Const.N,(i+1) * bandwidth); j++) {
                Centroid[i].addPoint(data[datapoints.get(j).index]);
                cnt++;
            }
            Centroid[i].divide(cnt);
        }
    }
   
    /******************* DIFFERENT K-MEAN ALGORITHMS ************************/
    public static void kMeanAlgorithm() {
        Point[] nextCentroid = new Point[Const.K];
        int[] noOfPoint = new int[Const.K];
        for(int i = 0; i < Const.K; i++) {
            nextCentroid[i] = new Point(Const.D);
        }
        double prevSSE = 0;
        // iterate the K-mean process Const.max_it times
        for(int iter = 0; iter < Const.max_it; iter++) {
            for(int i = 0; i < Const.K; i++) {
                nextCentroid[i].setZero();
                noOfPoint[i] = 0;
            }
            for(int i = 0; i < Const.N; i++) { //find the class label for Point i
                double minDistance = data[i].getDistance(Centroid[0]);
                int curClassLabel = 0;
                for(int j = 1; j < Const.K; j++) {
                    double distance = data[i].getDistance(Centroid[j]);
                    if (distance < minDistance) {
                        minDistance = distance;
                        curClassLabel = j;
                    } 
                }
                //add point i to class label curClassLabel
                nextCentroid[curClassLabel].addPoint(data[i]);
                noOfPoint[curClassLabel]++;
                data[i].label = curClassLabel;
            }
            
            // update the centroid
            for(int i = 0; i < Const.K; i++) {
                if (noOfPoint[i] > 0) {
                    nextCentroid[i].divide(noOfPoint[i]);
                    Centroid[i].setData(nextCentroid[i]);
                }
            }
            if (iter == 0) prevSSE = getSSEError();
            else {
                double curSSE = getSSEError();
                if (Math.abs(curSSE - prevSSE) < eps) {
                    System.out.println(iter + " iterations.");
                    break;
                }
                prevSSE = curSSE;
            }
        }
    }
    
    public static void kMeanAlgorithmTriangleInequality() {
        Point[] nextCentroid = new Point[Const.K];
        int[] noOfPoint = new int[Const.K];
        double[] temp = new double[Const.K];
        double[] upperBound = new double[Const.N];
        double[][] lowerBound = new double[Const.N][Const.K];
        double[][] centerDist = new double[Const.K][Const.K];
        double[] minDistToCenter = new double[Const.K];
        boolean[] notUpdate = new boolean[Const.N];
        
        //initilize
        for(int i = 0; i < Const.K; i++) {
            nextCentroid[i] = new Point(Const.D);
        }
        for(int i = 0; i < Const.N; i++) {
            notUpdate[i] = true;
        }
        for(int i = 0; i < Const.N; i++) { //find the class label for Point i
           double minDistance = data[i].getDistance(Centroid[0]);
           lowerBound[i][0] = minDistance;
           int curClassLabel = 0;
           for(int j = 1; j < Const.K; j++) {
               double distance = data[i].getDistance(Centroid[j]);
               lowerBound[i][j] = distance;
               if (distance < minDistance) {
                   minDistance = distance;
                   curClassLabel = j;
               } 
           }
           //add point i to class label curClassLabel
           data[i].label = curClassLabel;
           upperBound[i] = minDistance;
       }
       double prevSSE = 0; 
       for(int iter = 0; iter < Const.max_it; iter++) {
            // update the centroid
            for(int i = 0; i < Const.K; i++) {
                nextCentroid[i].setZero();
                noOfPoint[i] = 0;
            }
            for(int i = 0; i < Const.N; i++) {
                nextCentroid[ data[i].label ].addPoint(data[i]);
                noOfPoint[ data[i].label]++;
            }
            for(int i = 0; i < Const.K; i++) {
                if (noOfPoint[i] > 0) {
                    nextCentroid[i].divide(noOfPoint[i]);
                }
            }
            for(int center = 0; center < Const.K; center++) {
                temp[center] = Centroid[center].getDistance(nextCentroid[center]);
            }
            for(int i = 0; i < Const.N; i++) {
                for(int center = 0; center < Const.K; center++) {
                    lowerBound[i][center] = Math.max(0.0, lowerBound[i][center] - temp[center]);
                }
                
                upperBound[i] = upperBound[i] + temp[data[i].label];
                notUpdate[i] = true;
            }
            if (iter == Const.max_it - 1) {
                System.out.println(iter + " iterations.");
                break;
            }
            //System.out.println();
            for(int i = 0; i < Const.K; i++) {
                Centroid[i].setData(nextCentroid[i]);
            }
            //step 1
            for(int center1 = 0; center1 < Const.K; center1++) {
                if (center1 != 0) {
                    minDistToCenter[center1] = Centroid[center1].getDistance(Centroid[0]);
                } else {
                    minDistToCenter[center1] = Centroid[center1].getDistance(Centroid[1]);
                }
                
                for(int center2 = 0; center2 < Const.K; center2++) {
                    
                    centerDist[center1][center2] = Centroid[center1].getDistance(Centroid[center2]);
                    if (center1 != center2) {
                        if (centerDist[center1][center2] < minDistToCenter[center1]) {
                            minDistToCenter[center1] = centerDist[center1][center2];
                        }
                    }
                }
                minDistToCenter[center1] /= 2.0;
            }
            
            //step 3
            for(int i = 0; i < Const.N; i++) {
                if (upperBound[i] > minDistToCenter[data[i].label]) {
                    for(int center = 0; center < Const.K; center++) {
                        if (data[i].label != center
                                && upperBound[i] > lowerBound[i][center]
                                && upperBound[i] > 0.5 * centerDist[data[i].label][center]) {
                           
                            //valid point
                            double distance;
                            if (notUpdate[i]) {
                                distance = data[i].getDistance(Centroid[data[i].label]);
            //                    lowerBound[i][data[i].label] = distance;
                                notUpdate[i] = false;
                            } else {
                                distance = upperBound[i];
                            }
                            if (distance > lowerBound[i][center] ||
                                distance > 0.5 * centerDist[data[i].label][center]) {
                                double new_distance = data[i].getDistance(Centroid[center]);
                                if (new_distance < distance) {
                                    data[i].label = center;
                                    upperBound[i] = new_distance;
                                }
                            }
                        }
                    }
                }
            }
            
            if (iter == 0) prevSSE = getSSEError();
            else {
                double curSSE = getSSEError();
                if (Math.abs(curSSE - prevSSE) < eps) {
                    System.out.println(iter + " iterations.");
                    break;
                }
                prevSSE = curSSE;
            }
        }
    }
    public static void main(String[] args) {
        // TODO code application logic here
        dataInput();
        minsseError = 1e16;
        for(int nrun = 0; nrun < Const.n_run; nrun++) {
            //generateKInitialCentroids();
            improvedKInitialCentroids();
            kMeanAlgorithmTriangleInequality();
            //kMeanAlgorithm();
            dataOutput();
        }
        System.out.println("Min SSE Error = " + minsseError);
    }
}
//
//Min SSE Error = 216650.96281054325
//BUILD SUCCESSFUL (total time: 17 seconds)
//
//Min SSE Error = 221409.76651052854
//BUILD SUCCESSFUL (total time: 12 seconds)
//
class DataPoint implements Comparable<DataPoint> {
    public double distance;
    public int index;
    DataPoint() {
        distance = 0;
        index = 0;
    }
    DataPoint(double distance, int index) {
        this.distance = distance;
        this.index = index;
    }
    public int compareTo(DataPoint B) {
        if (distance < B.distance || (distance == B.distance && index < B.index)) return -1;
        else if (distance == B.distance && index == B.index) return 0;
        else return 1;
    }
}