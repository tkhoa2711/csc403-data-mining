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
    
    public static void dataInput() {
        try {
            Scanner sc = new Scanner(new File("dna.txt"));
            Const.N = sc.nextInt();
            Const.D = sc.nextInt();
            Const.C = sc.nextInt();
            data = new Point[Const.N];
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
    public static void generateKInitialCentroids() {
        Centroid = new Point[Const.K];
        // set the K-centroids to the first K-items
        
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
    
    public static void kMeanAlgorithm() {
        Point[] nextCentroid = new Point[Const.K];
        int[] noOfPoint = new int[Const.K];
        for(int i = 0; i < Const.K; i++) {
            nextCentroid[i] = new Point(Const.D);
        }
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
        }
    }
    public static double minsseError;
    public static void dataOutput() {
        for(int i = 0; i < Const.N; i++) {
     //       System.out.println("Class " + i + " = " + data[i].label);
        }
        double sseError = 0;
        for(int i = 0; i < Const.N; i++) {
            sseError += data[i].getDistance(Centroid[data[i].label]); 
        }
        minsseError = Math.min(minsseError,sseError);
        System.out.println("SSE = " + sseError);
    }
    public static void main(String[] args) {
        // TODO code application logic here
        dataInput();
        minsseError = 1e16;
        for(int nrun = 0; nrun < Const.n_run; nrun++) {
            generateKInitialCentroids();
            kMeanAlgorithm();
            dataOutput();
        }
        System.out.println("Min SSE Error = " + minsseError);
    }
}

//209556.5906846869