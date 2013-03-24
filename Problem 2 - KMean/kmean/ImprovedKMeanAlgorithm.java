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
    public static double eps = 1;
    /*
     * This function computes all distance between Centroids, and computes 
     * the array minDistToCenter[x] = 0.5 * min(dist[x][x'])
     * Complexity: O(K^2*D)
     */
    public static void calculateCenterToCenterDistance(Point[] Centroid, Point[] data) {   
        for(int center1 = 0; center1 < Const.K; center1++) {
            minDistToCenter[center1] = 1e16;
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
    
    /*
     * This method updates the centroids for the next iteration, as well as the
     * upperBound and lowerBound for each data point and its centroid.
     * Complexity: O(NK + KD)
     */
    public static void updateCentroids(Point[] Centroid, Point[] data) {
        double[] temp = new double[Const.K];
        int[] noOfPoint = new int[Const.K];
        Point[] nextCentroid = new Point[Const.K];
        
        /*
         * nextCentroid[i] indicates the mean of the clusters i
         */
        for(int i = 0; i < Const.K; i++) {
            nextCentroid[i] = new Point(Const.D);
        }
        for(int i = 0; i < Const.K; i++) {
            nextCentroid[i].setZero();
            noOfPoint[i] = 0;
        }
        for(int i = 0; i < Const.N; i++) {
            nextCentroid[ data[i].label ].addPoint(data[i]);
            noOfPoint[ data[i].label]++;
        }
        for(int i = 0; i < Const.K; i++) {                          
            if (noOfPoint[i] > 0) {
                nextCentroid[i].divide(noOfPoint[i]);
            }
        }
        /*
         * the temp[c] = distance between current iteration's center
         * and next iteration's center.
         */
        for(int center = 0; center < Const.K; center++) {           
            temp[center] = Centroid[center].getDistance(nextCentroid[center]);
        }
        
        /*
         * Update the lowerbound and upperBound and reset the notUpdated array
         * l(x,c) = l(x,c) - d(c, m(c))
         * u(x) = u(x) + d(m(c(x)),c)
         */
        for(int i = 0; i < Const.N; i++) {                          
            for(int center = 0; center < Const.K; center++) {
                lowerBound[i][center] = lowerBound[i][center] - temp[center];
            }
            upperBound[i] = upperBound[i] + temp[data[i].label];
            notUpdate[i] = true;
        }
        
        /*
         * update the new centroids
         */
        for(int i = 0; i < Const.K; i++) {                          
            Centroid[i].setData(nextCentroid[i]);
        }
        temp = null;
        noOfPoint = null;
        nextCentroid = null;
        System.gc();
    }
    /* 
     * This method clusters the data points based on the K initial centroids
     * using the triangle inequality approaches for K-mean algorithm
     * 
     * The complexity is approximately O(n_iter * K * max(N,D)).
     */
    public static Point[] clustering(Point[] Centroid, Point[] data) {
        upperBound = new double[Const.N];
        lowerBound = new double[Const.N][Const.K];
        centerDist = new double[Const.K][Const.K];
        minDistToCenter = new double[Const.K];
        notUpdate = new boolean[Const.N];
        
        //initilize
        for(int i = 0; i < Const.N; i++) {
            notUpdate[i] = true;
        }
        /* 
         * Assign each data point to its closest center
         * Compute the upperBound and lowerBound for each point and center
         */
        for(int i = 0; i < Const.N; i++) {
           double minDistance = data[i].getDistance(Centroid[0]);
           lowerBound[i][0] = minDistance;
           int closestLabel = 0;
           for(int j = 1; j < Const.K; j++) {
               double distance = data[i].getDistance(Centroid[j]);
               lowerBound[i][j] = distance;
               if (distance < minDistance) {
                   minDistance = distance;
                   closestLabel = j;
               } 
           }
           /*
            * add point i to class label closestLabel
            */
           data[i].label = closestLabel;
           upperBound[i] = minDistance;
       }
        
       double prevSSE = 0;  // previous SSE to check the convergence
       
       for(int iter = 0; iter < Const.max_it; iter++) {
            calculateCenterToCenterDistance(Centroid, data);

            for(int i = 0; i < Const.N; i++) {
                /* We eliminate all points x where upperBound[x] <= minDistToCenter[x] */
                if (upperBound[i] > minDistToCenter[data[i].label]) {
                    for(int center = 0; center < Const.K; center++) {
                        /* 
                         * We only consider center c where 
                         * c != c(x), 
                         * upperBound(x) > lowerBound(x,c) and
                         * upperBound(x) > 1/2 * d(c(x),c)
                         */
                        if (data[i].label != center
                                && upperBound[i] > lowerBound[i][center]
                                && upperBound[i] > 0.5 * centerDist[data[i].label][center]) {
                           
                            double distance;
                            if (notUpdate[i]) { /* if this point has not been updated */
                                /* compute the real distance */
                                distance = data[i].getDistance(Centroid[data[i].label]);  
                                
                                /* 
                                 * Everytime d(x,c) is computed, we need to 
                                 * update the lowerBound.
                                 * The upperBound is updated whenever d(x,c(x)) 
                                 * is computed or c(x) changes.
                                 */
                                lowerBound[i][data[i].label] = distance;
                                upperBound[i] = distance;
                                notUpdate[i] = false;
                            } else {
                                distance = upperBound[i];
                            }
                            if (distance > lowerBound[i][center] ||
                                distance > 0.5 * centerDist[data[i].label][center]) {
                                /* compute the real distance */
                                double new_distance = data[i].getDistance(Centroid[center]);
                                lowerBound[i][center] = new_distance;
                                
                                /* Update the class label and the upperBound */
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
                    break;
                }
                prevSSE = curSSE;
            }
       }
       
       /* Free memory using the garbage collector */
       centerDist = null;
       minDistToCenter = null;
       notUpdate = null;
       lowerBound = null;
       upperBound = null;
       System.gc();
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
