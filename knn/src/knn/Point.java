/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

import java.math.*;
/**
 *
 * @author Vu
 */
public class Point {
    public double data[];
    public double label;
    
    public Point() {
        data = new double[0];
        
    }
    
    public Point(double[] data) {
        this.data = data;
    }
    
    public Point(int size) {
        data = new double[size];
    }
    
    public double[] getData(){
        return this.data;
    }
    
    public double getLabel(){
        return this.label;
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
    
    public void setLabel(double label) {
        this.label = label;
    }
    
    public double getValue(int index) {
        return data[index];
    }
    
    public double getDistance(Point B) {
        double res = 0;
        for(int i = 0; i < data.length; i++) {
            res = res + Math.sqrt((getValue(i) - B.getValue(i)) * (getValue(i) - B.getValue(i)));
        }
        return res;
    }
    
    public double getL1Distance(Point B) {
        double res = 0;
        for(int i = 0; i < data.length; i++) {
            res = res + (getValue(i) - B.getValue(i));
        }
        return res;
    }
    
    public double getEucliDistance(Point B) {
        double res = 0;
        for(int i = 0; i < data.length; i++) {
            res = res + (getValue(i) - B.getValue(i)) * (getValue(i) - B.getValue(i));
        }
        res = Math.sqrt(res);
        return res;
    }
    
    public double getCosDistance(Point B) {
        double res = 0;
        res = this.getDotProduct(B)/(this.getAbs()*B.getAbs());
        return res;
    }
    
    public double getDotProduct(Point B){
        double res = 0;
        for(int i = 0; i < data.length; i++) {
            res = res + this.getValue(i)*B.getValue(i);
        }
        return res;              
    }
    
    public double getAbs(){
        double res = 0;
        for(int i = 0; i < data.length; i++) {
            res = res + this.getValue(i)*this.getValue(i);
        }
        res = Math.sqrt(res);
        return res;
    }
    
    
}
