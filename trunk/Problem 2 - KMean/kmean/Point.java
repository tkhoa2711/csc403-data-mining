/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kmean;

import java.math.*;
/**
 *
 * @author Vu
 */
public class Point {
    public double data[];
    public int label;
    
    public Point() {
        data = new double[0];
    }
    public Point(double[] a) {
        this.data = data;
    }
    public Point(int size) {
        data = new double[size];
    }
    /* perform the addition (this + B) */
    public void addPoint(Point B) {
        for(int i = 0; i < data.length; i++) {
            setValue(i, getValue(i) + B.getValue(i));
        }
    } 
    
    /* divide each dimension to the real value */
    public void divide(double value) {
        for(int i = 0; i < data.length; i++) {
            setValue(i, getValue(i) / value);
        }
    }
    
    /* Set all zero to all dimensions*/
    public void setZero() {
        for(int i = 0; i < data.length; i++) {
            setValue(i, 0);
        }
    }
    
    /* Set the data of point B to its data */
    public void setData(Point B) {
        for(int i = 0; i < data.length; i++) {
            setValue(i, B.getValue(i));
        }
    }
    
    /* Set the index-th value */
    public void setValue(int index, double value) {
        data[index] = value;
    }  
    
    /* Get the index-th value */
    public double getValue(int index) {
        return data[index];
    }
    
    /* Get the euclidean distance to point B */
    public double getDistance(Point B) {
        double res = 0;
        for(int i = 0; i < data.length; i++) {
            res = res + (getValue(i) - B.getValue(i)) * (getValue(i) - B.getValue(i));
        }
        return Math.sqrt(res);
    }
}
