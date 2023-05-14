package main.java.index;

import java.io.Serializable;
import java.util.Hashtable;

public class OctTreeEntry implements Serializable {
    private String page;
    private final Point3D location;
    private final Hashtable<String, Object> htblColNameValue;
    private boolean overflown;
    OctTreeEntry nextOverflow;

    public OctTreeEntry(String page, Hashtable<String, Object> htblColNameValue, Point3D location){
        this.page = page;
        this.htblColNameValue = htblColNameValue;
        this.location = location;
    }

    public  String getPage(){
        return page;
    }

    public Hashtable<String, Object> getHtblColNameValue() {
        return htblColNameValue;
    }

    public Point3D getLocation() {
        return location;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public boolean isOverflown() {
        return overflown;
    }

    public void insertOverflow(String pageName, Point3D location, Hashtable<String, Object> htblColNameValue){
        if(this.nextOverflow == null){
            nextOverflow = new OctTreeEntry(pageName, htblColNameValue, location);
            this.overflown = true;
            return;
        }
        nextOverflow.insertOverflow(pageName, location, htblColNameValue);
    }

    public void shiftByOnePage(String strClusteringKey, Hashtable<String, Object> htblColNameValues){
        if(htblColNameValues.get(strClusteringKey).equals(htblColNameValue.get(strClusteringKey))){
            String[] pageName = page.split("_");
            page = pageName[0] + (Integer.parseInt(pageName[1]) + 1);
            return;
        }
        nextOverflow.shiftByOnePage(strClusteringKey, htblColNameValues);
    }

    public String toString(){
        return "{ Location=" + location + ", Reference=" + page +
                ", htblColNameValue=" + htblColNameValue + " }";
    }
}
