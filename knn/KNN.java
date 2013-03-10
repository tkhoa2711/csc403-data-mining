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

    public static void parseOptions(String trainData, String testData, int k, int d, String[] args) {
        try {
            trainData = args[0];
            testData = args[1];
            for (int i = 2; i < args.length; i++) {
                if (args[i].equals("-k"))
                    k = Integer.parseInt(args[i++]);
                else if (args[i].equals("-d"))
                    d = Integer.parseInt(args[i++]);                
            }
        } catch (Exception e) {
            System.out.println("Error parsing command line options");
        }
    }

    public static void main(String[] args) {
        
        String trainData = "";
        String testData = "";
        parseOptions(trainData, testData, Const.K, Const.D, args);

        dataTrain = dataInput(trainData);
        dataTest = dataInput(testData);
        double[] predictedLabel = new double[dataTest.length];
        double accuracy;
        double timecost = 0;
        
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
            
            //get index of K nearesr neighbor 
            int[] neighbor = new int[Const.K];
            neighbor = getMinIndex(Const.K,distance);
            
            //get label of K nearest neighbor
            double[] neighborLabel = new double[Const.K];
            for(int j = 0; j<Const.K;j++){
                neighborLabel[j] = dataTrain[neighbor[j]].getLabel();
            }
            
            //assign predicted label to test point 
            predictedLabel[i] = identifyLabel(neighborLabel);
            //compute accuracy of the predicted label
            accuracy = computeAcc(dataTest,predictedLabel);
            
            //print output file
            dataOutput(dataTest,predictedLabel,accuracy,timecost);
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
    
    public static double identifyLabel(double[] neighborLabel){
        double predictedLabel = -1;
            Label[] labelList = new Label[Const.C];
            int countlabel = 0;
            boolean addMem = false;
            
            for(int i = 0; i<neighborLabel.length;i++){
                addMem = false;
                for (int j=0; j<countlabel;j++){
                    if (neighborLabel[i] == labelList[j].getLabel()){
                        labelList[j].addMember();
                        addMem = true;
                        break;
                    }                        
                }
                if (!addMem){
                    labelList[countlabel] = new Label(neighborLabel[i]); 
                    labelList[countlabel].addMember();
                    countlabel++;
                }
            }
            int maxMemberCount = 0;
            int index = -1;
            for(int i = 0; i < countlabel; i++){
                if(labelList[i].getMembersCount()> maxMemberCount){
                    maxMemberCount = labelList[i].getMembersCount();
                    index = i;
                }   
            }
            predictedLabel = labelList[index].getLabel();
        return predictedLabel;
    }
    
    public static double computeAcc(Point[] trueLabel, double[] predictedLabel){
        double acc = 0;
        int truecount=0;
        for(int i=0; i < trueLabel.length;i++){
            if (trueLabel[i].getLabel()==predictedLabel[i])
                truecount++;
        }
        acc = truecount/trueLabel.length;
        return acc;
        
    }
    
    public static void dataOutput(Point[] dataTest, double[] predictedLabel, double accuracy, double timeCost){
        
    }
}
