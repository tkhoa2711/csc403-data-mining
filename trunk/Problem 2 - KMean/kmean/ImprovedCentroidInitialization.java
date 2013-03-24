/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kmean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Vu
 */
public class ImprovedCentroidInitialization {
    
    public static Point[] init(Point[] data) {
        Point[] Centroid = new Point[Const.K];
        double minValue = 1e16;
        
        /* find the minimum coordinate of all data points */
        for(int i = 0; i < Const.N; i++)
            for(int j = 0; j < Const.D; j++) {
                if (minValue > data[i].getValue(j)) {
                    minValue = data[i].getValue(j);
                }
            }
        /* move all data so that they have positive coordinates */
        for(int i = 0; i < Const.N; i++) {
            for(int j = 0; j < Const.D; j++) {
                data[i].setValue(j, data[i].getValue(j) - minValue + 1);
            }
        }
        Point Origin = new Point(Const.D);
        Origin.setZero();
        
        /* calculate the distance to the origin from each data point */
        List<DataPoint> datapoints = new ArrayList<DataPoint>();
        for(int i = 0; i < Const.N; i++) {
            datapoints.add(new DataPoint(data[i].getDistance(Origin),i));
        }
        
        /* recover original data */
        for(int i = 0; i < Const.N; i++) {
            for(int j = 0; j < Const.D; j++) {
                data[i].setValue(j, data[i].getValue(j) + minValue - 1);
            }
        }
        /* sort the points and choose the K-initial centroids */
        Collections.sort(datapoints);
        int bandwidth = Const.N / Const.K;
        if (Const.N % Const.K != 0) bandwidth++;
        for(int i = 0; i < Const.K; i++) {
            Centroid[i] = new Point(Const.D);
            int cnt = Math.min(Const.N,(i+1)*bandwidth) - i*bandwidth;
            Centroid[i].setData(data[datapoints.get(i*bandwidth+cnt/2-1).index]);
        }
        return Centroid;
    }
}
class DataPoint implements Comparable<DataPoint> {
      public double distance;
      public int index;
      DataPoint() {
          distance = 0;
          index = 0;
      }
      DataPoint(double distance, int index) {
          this.distance = distance;
          this.index = index;
      }
      public int compareTo(DataPoint B) {
          if (distance < B.distance || (distance == B.distance && index < B.index)) return -1;
          else if (distance == B.distance && index == B.index) return 0;
          else return 1;
      }
  }
