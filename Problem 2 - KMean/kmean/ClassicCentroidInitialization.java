/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kmean;

/**
 *
 * @author Vu
 */
public class ClassicCentroidInitialization {
    public static Point[] init(Point[] data) {
        Point[] Centroid = new Point[Const.K];       
        //shuffle the sequence to select randomly K points as Centroid
        int[] Index = new int[Const.N];
        for(int i = 0; i < Const.N; i++) {
            Index[i] = i;
            int j = (int)(Math.random() * i);
            int tmp = Index[i];
            Index[i] = Index[j];
            Index[j] = tmp;
        }
        
        for(int i = 0; i < Const.K; i++) {
            Centroid[i] = new Point(Const.D);
            Centroid[i].setData(data[Index[i]]);
        }
        return Centroid;
    }
}
