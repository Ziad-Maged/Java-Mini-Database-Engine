package main.java.index;

import java.io.Serializable;
import java.util.Hashtable;

public class OctTreeEntry implements Serializable {
    private String page;
    private final Point3D location;
    private final Hashtable<String, Object> htblColNameValue;

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

    public String toString(){
        return "{ Location=" + location + ", Reference=" + page +
                ", htblColNameValue=" + htblColNameValue + " }";
    }
}
