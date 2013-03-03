/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import kNN.Point;

/**
 *
 * @author KhecKhec
 */
public class KNN {
/**
     * @param args the command line arguments
     */
    public static Point[] dataTrain;
    public static Point[] dataTest;
    public static Point[] Centroid;
    
    public static Point[] dataInput(String file) {
        try {
            Scanner sc = new Scanner(new File(file));
            Const.N = sc.nextInt();
            Const.D = sc.nextInt();
            Const.C = sc.nextInt();
            Point[] data = new Point[Const.N];
            for(int i = 0; i < Const.N; i++) {
                data[i] = new Point(Const.D);
                
                for(int j = 0; j < Const.D; j++) {
                    data[i].setValue(j, sc.nextDouble());
                }
                data[i].setLabel(sc.nextDouble());
                
                sc.nextDouble();
            }
            return data;
        } catch (IOException e) {
            System.out.println("There is no input file");
            return null;
        }
        
    }
    public static void main(String[] args) {
        
        String traindata = "";
        String testdata = "";
        dataTrain = dataInput(traindata);
        dataTest = dataInput(testdata);
        
        for (int i = 0; i<dataTest.length;i++){                     
            
            double[] distance = new double[dataTrain.length];
            
            for (int j = 0; i<dataTrain.length;j++){
                
                switch(Const.metric){
                    case 0: 
                        distance[j]= dataTest[i].getCosDistance(dataTrain[j]);
                    case 1: 
                        distance[j]= dataTest[i].getL1Distance(dataTrain[j]);
                    case 2: 
                        distance[j]= dataTest[i].getEucliDistance(dataTrain[j]);                
                }
                    
            }
            
            int[] neighbor = new int[Const.K];
            neighbor = getMinIndex(Const.K,distance);
            
            double[] neighborLabel = new double[Const.K];
            for(int j = 0; j<Const.K;j++){
                neighborLabel[j] = dataTest[neighbor[j]].getLabel();
            }
            
            
            
        }
    }
    
    public static int[] getMinIndex(int k, double[] d){
        int[] res = new int[k];
        boolean[] isMin = new boolean[d.length];
        double min=-1;
        
        for (int j=0;j<d.length;j++){
            isMin[j]=false;
        }
        
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
    
    
}
