/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

/**
 *
 * @author KhecKhec
 */
public class Label {
    public double label;
    public double weight;
    
    
    public Label(double label) {
        this.label = label;
        weight = 0;
    }
    
    public Label(double label,double weight) {
        this.label = label;
        this.weight = weight;
    }
    
    public void setWeight(double weight){
        this.weight = weight;
    }
    
    public double getLabel(){
        return this.label;
    }
    
    public double getWeight(){
        return this.weight;
    }
    
    public void addMember(double weight){
        this.weight= this.weight+weight ;
    }
}
