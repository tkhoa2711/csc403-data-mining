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
        
        Point Origin = new Point(Const.D);
        Origin.setZero();
        
        List<DataPoint> datapoints = new ArrayList<DataPoint>();
        for(int i = 0; i < Const.N; i++) {
            datapoints.add(new DataPoint(data[i].getDistance(Origin),i));
        }
        Collections.sort(datapoints);
        int bandwidth = Const.N / Const.K;
        if (Const.N % Const.K != 0) bandwidth++;
        for(int i = 0; i < Const.K; i++) {
            Centroid[i] = new Point(Const.D);
            Centroid[i].setZero();
            int cnt = 0;
            for(int j = i * bandwidth; j < Math.min(Const.N,(i+1) * bandwidth); j++) {
                Centroid[i].addPoint(data[datapoints.get(j).index]);
                cnt++;
            }
            Centroid[i].divide(cnt);
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
