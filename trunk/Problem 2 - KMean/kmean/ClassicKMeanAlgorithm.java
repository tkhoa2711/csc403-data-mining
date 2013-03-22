/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kmean;

/**
 *
 * @author Vu
 */
public class ClassicKMeanAlgorithm {
    public static double eps = 0;
    
    public static Point[] clustering(Point[] Centroid, Point[] data) {
        System.out.println("CLUSTERING DATA...");
        
        Point[] nextCentroid = new Point[Const.K];
        int[] noOfPoint = new int[Const.K];
        for(int i = 0; i < Const.K; i++) {
            nextCentroid[i] = new Point(Const.D);
        }
        double prevSSE = 0;
        // iterate the K-mean process Const.max_it times
        for(int iter = 0; iter < Const.max_it; iter++) {
            int dem = Point.nOperation;
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
            if (iter == 0) prevSSE = getSSEError(Centroid, data);
            else {
                double curSSE = getSSEError(Centroid, data);
                if (Math.abs(curSSE - prevSSE) < eps) {
                    System.out.println(iter + " iterations.");
                    break;
                }
                prevSSE = curSSE;
            }
        }
        return Centroid;
    }
    public static double getSSEError(Point[] Centroid, Point[] data) {
        double sseError = 0;
        for(int i = 0; i < Const.N; i++) {
            sseError += data[i].getDistance(Centroid[data[i].label]) * data[i].getDistance(Centroid[data[i].label]); 
        }
        return sseError;
    }
}
