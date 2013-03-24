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
    public static Point[] minCentroid;
    public static double minsseError;
    public static double eps = 1;
    public static long estimatedTime;
    public static void main(String[] args) {
        dataInput();
        minsseError = 1e16;
        minCentroid = new Point[Const.K];
        for(int i = 0; i < Const.K; i++) {
            minCentroid[i] = new Point(Const.D);
        }
        long startTime = System.nanoTime();    
        
        for(int nrun = 0; nrun < Const.n_run; nrun++) { /* Run the algorithm n_run times */
            if (nrun == 0) {
                Centroid = ImprovedCentroidInitialization.init(data);
            } else {
                Centroid = ClassicCentroidInitialization.init(data);
            }            
            Centroid = ImprovedKMeanAlgorithm.clustering(Centroid, data);            
            updateSSE();
        }
        
        estimatedTime = System.nanoTime() - startTime;
        dataOutput();
    }
    /*************************DATA I/O***************************/
    public static void dataInput() {
        try {
            String FileName = "glass.txt";
            Scanner sc = new Scanner(new File(FileName));
            Const.N = sc.nextInt(); Const.D = sc.nextInt(); Const.C = sc.nextInt();
            data = new Point[Const.N];
            for(int i = 0; i < Const.N; i++) {
                data[i] = new Point(Const.D);
                data[i].label = 0;
                for(int j = 0; j < Const.D; j++) {
                    data[i].setValue(j, sc.nextDouble());
                }
                if (Const.C == 1) {
                    sc.nextDouble();
                }
            }
        } catch (IOException e) {
            System.out.println("There is no input file");
        }
    }
    
    public static void dataOutput() {
        System.out.println("Execution time =\t " + estimatedTime/1000000000.0 + " seconds.");
        System.out.println("Within-cluster SSE =\t " + minsseError);
        System.out.println("(Final Center Points:");
        double[] WSS = new double[Const.K];
        for(int i = 0; i < Const.K; i++) {
            WSS[i] = 0;
        }
        for(int i = 0; i < Const.N; i++) {
            WSS[data[i].label] += data[i].getDistance(minCentroid[data[i].label]) *
                                  data[i].getDistance(minCentroid[data[i].label]);
        }
        for(int i = 0; i < Const.K; i++) {
            System.out.print("\t" + i + "\t[ ");
            for(int j = 0; j < Const.D; j++)
                System.out.printf("%.3f ", minCentroid[i].getValue(j));
            System.out.printf("]\tSSE = %.3f\n",WSS[i]);
        }
        System.out.println(")");
        
        System.out.println("Point\tCenter\tSquared Dist");
        System.out.println("----\t----\t-----------");
        for(int i = 0; i < Const.N; i++) {
            System.out.printf("%d\t%d\t%.3f\n",i,data[i].label,data[i].getDistance(minCentroid[data[i].label]) *
                                                               data[i].getDistance(minCentroid[data[i].label]));
        }
    }
    
    public static void updateSSE() {
        double sseError = getSSEError();
        if (minsseError < sseError) {
            minsseError = sseError;
            for(int i = 0; i < Const.K; i++) {
                minCentroid[i].setData(Centroid[i]);
            }
        }
    }

    public static double getSSEError() {
        double sseError = 0;
        for(int i = 0; i < Const.N; i++) {
            sseError += data[i].getDistance(Centroid[data[i].label]) * data[i].getDistance(Centroid[data[i].label]); 
        }
        
        minsseError = Math.min(minsseError, sseError);
        return sseError;
    }   
}

