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
        
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Min SSE Error = " + minsseError);
        System.out.println("Time elapsed = " + estimatedTime/1000000000.0 + " seconds.");
    }
    /*************************DATA I/O***************************/
    public static void dataInput() {
        try {
            String FileName = "isolet.txt";
            System.out.println("Running on " +FileName);
            Scanner sc = new Scanner(new File(FileName));
            Const.N = sc.nextInt(); Const.D = sc.nextInt(); Const.C = sc.nextInt();
            data = new Point[Const.N];
            System.out.println("nPoint = " + Const.N);
            System.out.println("nDimension = " + Const.D);
            System.out.println("nClass = " + Const.K);
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

