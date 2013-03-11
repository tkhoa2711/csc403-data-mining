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
	
	public static Point[] dataInput(String file) {
		try {
			Scanner sc = new Scanner(new File(file));
			N = sc.nextInt();
			D = sc.nextInt();
			C = sc.nextInt();
			Point[] data = new Point[N];
			for(int i = 0; i < N; i++) {
				data[i] = new Point(D);
				for(int j = 0; j < D; j++) {
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
					K = Integer.parseInt(args[i+1]);
					i++;
				} else if (args[i].equals("-d")) {
					METRIC = Integer.parseInt(args[i+1]);
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
			if (!labelList.contains(Double.valueOf(dataTrain[i].getLabel())))
				labelList.add(Double.valueOf(dataTrain[i].getLabel()));
		}
		C = labelList.size();

		double[] predictedLabel = new double[dataTest.length];
		
		for (int i = 0; i<dataTest.length;i++){  
			double[] distance = new double[dataTrain.length];
			for (int j = 0; j<dataTrain.length;j++){
				switch(METRIC){
					case 0: 
						distance[j]= dataTest[i].getCosDistance(dataTrain[j]);
					case 1: 
						distance[j]= dataTest[i].getL1Distance(dataTrain[j]);
					case 2: 
						distance[j]= dataTest[i].getEucliDistance(dataTrain[j]);                
				}
			}
			
			//get index of K nearesr neighbor 
			int[] neighbor = new int[K];
			neighbor = getMinIndex(K,distance);
			
			//get label of K nearest neighbor
			double[] neighborLabel = new double[K];
			for(int j = 0; j<K;j++){
				neighborLabel[j] = dataTrain[neighbor[j]].getLabel();
			}
			
			//assign predicted label to test point 
			predictedLabel[i] = identifyLabel(neighborLabel);
		}

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		//compute accuracy of the predicted label
		double accuracy = computeAcc(dataTest,predictedLabel);
		
		//print output file
		predictionFile = testDataFile.concat("_prediction.txt");
		dataOutput(predictionFile, dataTest, predictedLabel);

		System.out.println("Accuracy = " + (accuracy*100) + "%");;
		System.out.println("Timecost = " + ((double)duration/1000000000) + "seconds");
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
		Label[] labelList = new Label[C];
		int countlabel = 0;
		boolean addMem = false;
		
		for(int i = 0; i<labelList.length;i++){
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
			if (Double.valueOf(trueLabel[i].getLabel()).compareTo(Double.valueOf(predictedLabel[i])) == 0)
				truecount++;
		}
		acc = (double)truecount/trueLabel.length;
		return acc;
		
	}
	
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
}
