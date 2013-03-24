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
    public static double eps = 1;
    
    /* 
     * This method clusters the data points based on the K initial centroids
     * using basic K-mean algorithm
     * The complexity is approximately O(n_iter * K * N * D).
     */
    public static Point[] clustering(Point[] Centroid, Point[] data) {
        System.out.println("CLUSTERING DATA...");
        
        Point[] nextCentroid = new Point[Const.K];
        int[] noOfPoint = new int[Const.K];
        for(int i = 0; i < Const.K; i++) {
            nextCentroid[i] = new Point(Const.D);
        }
        double prevSSE = 0;
        
        for(int iter = 0; iter < Const.max_it; iter++) {
            for(int i = 0; i < Const.K; i++) {
                nextCentroid[i].setZero();
                noOfPoint[i] = 0;
            }
            for(int i = 0; i < Const.N; i++) { 
                /* 
                 * find the centroid with closest distance to data point i
                 * and assign i to this centroid
                 */
                double minDistance = data[i].getDistance(Centroid[0]);
                int closestLabel = 0;
                for(int j = 1; j < Const.K; j++) {
                    double distance = data[i].getDistance(Centroid[j]);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestLabel = j;
                    }
                }
                //add point i to class label closestLabel
                nextCentroid[closestLabel].addPoint(data[i]);
                noOfPoint[closestLabel]++;
                data[i].label = closestLabel;
            }
            
            /*
             * update the centroid
             */
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
