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
    public int membersCount;
    
    
    public Label(double label) {
        this.label = label;
        membersCount = 0;
    }
    
    public void setMembersCount(int count){
        this.membersCount = count;
    }
    
    public double getLabel(){
        return this.label;
    }
    
    public int getMembersCount(){
        return this.membersCount;
    }
    
    public void addMember(){
        this.membersCount ++ ;
    }
}
