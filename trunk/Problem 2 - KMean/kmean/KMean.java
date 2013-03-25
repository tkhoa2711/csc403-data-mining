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
    
    public static String InputFile = "glass.txt";
    public static String OutputFile = "KMean_output.txt";
    public static void main(String[] args) {
		// Parse command line options
		try {
			if (args.length < 2) {
				System.out.println("Insufficient command line options");				
				return;
			}

			trainDataFile = args[0];
			testDataFile = args[1];
			for (int i = 2; i < args.length; i++) {
				if (args[i].equals("-k")) {
					Const.K = Integer.parseInt(args[i+1]);
					i++;
				} else if (args[i].equals("-m")) {
					Const.max_it = Integer.parseInt(args[i+1]);
					i++;
				} else if (args[i].equals("-r")) {
					Const.n_run = Integer.parseInt(args[i+1]);
					i++;
				}
			}
		} catch (Exception e) {
			System.out.println("Error parsing command line options");
			return;
		}
		
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
            Scanner sc = new Scanner(new File(InputFile));
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
        try {
            PrintWriter pw = new PrintWriter(new File(OutputFile));
            System.out.println("Execution time =\t " + estimatedTime/1000000000.0 + " seconds.");
            pw.println("Within-cluster SSE =\t " + minsseError);
            pw.println("(Final Center Points:");
            double[] WSS = new double[Const.K];
            for(int i = 0; i < Const.K; i++) {
                WSS[i] = 0;
            }
            for(int i = 0; i < Const.N; i++) {
                WSS[data[i].label] += data[i].getDistance(minCentroid[data[i].label]) *
                                      data[i].getDistance(minCentroid[data[i].label]);
            }
            for(int i = 0; i < Const.K; i++) {
                pw.print("\t" + i + "\t[ ");
                for(int j = 0; j < Const.D; j++)
                    pw.printf("%.3f ", minCentroid[i].getValue(j));
                pw.printf("]\tSSE = %.3f\n",WSS[i]);
            }
            pw.println(")");

            pw.println("Point\tCenter\tSquared Dist");
            pw.println("----\t----\t-----------");
            for(int i = 0; i < Const.N; i++) {
                pw.printf("%d\t%d\t%.3f\n",i,data[i].label,data[i].getDistance(minCentroid[data[i].label]) *
                                                                   data[i].getDistance(minCentroid[data[i].label]));
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("IO Exception");
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

