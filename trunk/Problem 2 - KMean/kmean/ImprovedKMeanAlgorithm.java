/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kmean;

/**
 *
 * @author Vu
 */
public class ImprovedKMeanAlgorithm {
    public static double [][] centerDist;       //distance between centers
    public static double [] minDistToCenter;    // = minimum distance to other centers
    public static boolean [] notUpdate;
    public static double [][] lowerBound;
    public static double [] upperBound;
    public static double eps;
            
    public static void calculateCenterToCenterDistance(Point[] Centroid, Point[] data) {   //O(K^2*D)
        for(int center1 = 0; center1 < Const.K; center1++) {
            if (center1 != 0) {
                minDistToCenter[center1] = Centroid[center1].getDistance(Centroid[0]);
            } else {
                minDistToCenter[center1] = Centroid[center1].getDistance(Centroid[1]);
            }
            for(int center2 = 0; center2 < Const.K; center2++) {       
                centerDist[center1][center2] = Centroid[center1].getDistance(Centroid[center2]);
                if (center1 != center2) {
                    if (centerDist[center1][center2] < minDistToCenter[center1]) {
                        minDistToCenter[center1] = centerDist[center1][center2];
                    }
                }
            }
            minDistToCenter[center1] /= 2.0;
        }
    }
    
    public static void updateCentroids(Point[] Centroid, Point[] data) {
        double[] temp = new double[Const.K];
        int[] noOfPoint = new int[Const.K];
        Point[] nextCentroid = new Point[Const.K];
        // update the centroid
        for(int i = 0; i < Const.K; i++) {
            nextCentroid[i] = new Point(Const.D);
        }
        for(int i = 0; i < Const.K; i++) {                       //O(KD)
            nextCentroid[i].setZero();
            noOfPoint[i] = 0;
        }
        for(int i = 0; i < Const.N; i++) {                          //O(ND)
            nextCentroid[ data[i].label ].addPoint(data[i]);
            noOfPoint[ data[i].label]++;
        }
        for(int i = 0; i < Const.K; i++) {                          //O(KD)
            if (noOfPoint[i] > 0) {
                nextCentroid[i].divide(noOfPoint[i]);
            }
        }
        for(int center = 0; center < Const.K; center++) {           //O(KD)
            temp[center] = Centroid[center].getDistance(nextCentroid[center]);
        }
        for(int i = 0; i < Const.N; i++) {                          //O(NK)
            for(int center = 0; center < Const.K; center++) {
                lowerBound[i][center] = lowerBound[i][center] - temp[center];
            }

            upperBound[i] = upperBound[i] + temp[data[i].label];
            notUpdate[i] = true;
        }
        //System.out.println();
        for(int i = 0; i < Const.K; i++) {                          //O(KD)
            Centroid[i].setData(nextCentroid[i]);
        }
    }
    public static Point[] clustering(Point[] Centroid, Point[] data) {
        System.out.println("CLUSTERING DATA...");
        upperBound = new double[Const.N];
        lowerBound = new double[Const.N][Const.K];
        centerDist = new double[Const.K][Const.K];
        minDistToCenter = new double[Const.K];
        notUpdate = new boolean[Const.N];
        
        //initilize
        for(int i = 0; i < Const.N; i++) {
            notUpdate[i] = true;
        }
        for(int i = 0; i < Const.N; i++) { //find the class label for Point i
           double minDistance = data[i].getDistance(Centroid[0]);
           lowerBound[i][0] = minDistance;
           int curClassLabel = 0;
           for(int j = 1; j < Const.K; j++) {
               double distance = data[i].getDistance(Centroid[j]);
               lowerBound[i][j] = distance;
               if (distance < minDistance) {
                   minDistance = distance;
                   curClassLabel = j;
               } 
           }
           //add point i to class label curClassLabel
           data[i].label = curClassLabel;
           upperBound[i] = minDistance;
       }
       double prevSSE = 0; 
       
       for(int iter = 0; iter < Const.max_it; iter++) {
            calculateCenterToCenterDistance(Centroid, data);
            //step 3
            for(int i = 0; i < Const.N; i++) {
                double key = minDistToCenter[data[i].label];
                if (upperBound[i] > key) {
                    for(int center = 0; center < Const.K; center++) {
                        if (data[i].label != center
                                && upperBound[i] > lowerBound[i][center]
                                && upperBound[i] > 0.5 * centerDist[data[i].label][center]) {
                           
                            //valid point
                            double distance;
                            if (notUpdate[i]) {
                                distance = data[i].getDistance(Centroid[data[i].label]);  //O(ND)
                                lowerBound[i][data[i].label] = distance;
                                upperBound[i] = distance;
                                notUpdate[i] = false;
                            } else {
                                distance = upperBound[i];
                            }
                            if (distance > lowerBound[i][center] ||
                                distance > 0.5 * centerDist[data[i].label][center]) {
                                double new_distance = data[i].getDistance(Centroid[center]);
                                lowerBound[i][center] = new_distance;
                                if (new_distance < distance) {
                                    data[i].label = center;
                                    upperBound[i] = new_distance;
                                }
                            }
                        }
                    }
                }
            }
            updateCentroids(Centroid, data);
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
