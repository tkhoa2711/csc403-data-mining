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
    public static double eps = 1;
    public static void main(String[] args) {
        // TODO code application logic here
        dataInput();
        minsseError = 1e16;
        long startTime = System.nanoTime();    
        for(int nrun = 0; nrun < Const.n_run; nrun++) {
            //Centroid = ClassicCentroidInitialization.init(data);
            Centroid = ImprovedCentroidInitialization.init(data);
            Centroid = ImprovedKMeanAlgorithm.clustering(Centroid, data);
            //Centroid = ClassicKMeanAlgorithm.clustering(Centroid, data);
            dataOutput();
        }
        System.out.println("number of distance operation = " + Point.nOperation);
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Min SSE Error = " + minsseError);
        System.out.println("Time elapsed = " + estimatedTime/1000000000.0 + " seconds.");
    }
    /*************************DATA I/O***************************/
    public static void dataInput() {
        try {
            Scanner sc = new Scanner(new File("huge_test_data.txt"));
            Const.N = sc.nextInt(); Const.D = sc.nextInt(); Const.C = sc.nextInt();
            data = new Point[Const.N];
            System.out.println("nPoint = " + Const.N);
            System.out.println("nDimension = " + Const.D);
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
        double sseError = getSSEError();
        minsseError = Math.min(minsseError, sseError);
    }

    public static double minsseError;
    public static double getSSEError() {
        double sseError = 0;
        for(int i = 0; i < Const.N; i++) {
            sseError += data[i].getDistance(Centroid[data[i].label]) * data[i].getDistance(Centroid[data[i].label]); 
        }
        
        minsseError = Math.min(minsseError, sseError);
        return sseError;
    }   
}
