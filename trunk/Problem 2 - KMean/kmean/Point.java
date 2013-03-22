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
    public static int nOperation = 0;
    
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
    
    public void addPoint(Point B) {
        for(int i = 0; i < data.length; i++) {
            setValue(i, getValue(i) + B.getValue(i));
        }
    }
    
    public void divide(double value) {
        for(int i = 0; i < data.length; i++) {
            setValue(i, getValue(i) / value);
        }
    }
    
    public void setZero() {
        for(int i = 0; i < data.length; i++) {
            setValue(i, 0);
        }
    }
    public void setData(Point B) {
        for(int i = 0; i < data.length; i++) {
            setValue(i, B.getValue(i));
        }
    }
    public void setValue(int index, double value) {
        data[index] = value;
    }
    
    public double getValue(int index) {
        return data[index];
    }
    
    public double getDistance(Point B) {
        nOperation++;
        double res = 0;
        for(int i = 0; i < data.length; i++) {
            res = res + (getValue(i) - B.getValue(i)) * (getValue(i) - B.getValue(i));
        }
        return Math.sqrt(res);
    }
}
