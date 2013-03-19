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
    public double membersCount;
    
    
    public Label(double label) {
        this.label = label;
        membersCount = 0;
    }
    
    public Label(double label,double memberCount) {
        this.label = label;
        this.membersCount = memberCount;
    }
    
    public void setMembersCount(int count){
        this.membersCount = count;
    }
    
    public double getLabel(){
        return this.label;
    }
    
    public double getMembersCount(){
        return this.membersCount;
    }
    
    public void addMember(double weight){
        this.membersCount= this.membersCount+weight ;
    }
}
