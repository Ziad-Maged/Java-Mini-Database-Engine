package main.java.index;

import java.io.Serializable;

public class OctTreeEntry implements Serializable {
    private String page;
    private final Point3D location;
    private final Object objClusteringKeyValue;

    public OctTreeEntry(String page, Object objClusteringKeyValue, Point3D location){
        this.page = page;
        this.objClusteringKeyValue = objClusteringKeyValue;
        this.location = location;
    }

    public  String getPage(){
        return page;
    }

    public Object getObjClusteringKeyValue() {
        return objClusteringKeyValue;
    }

    public Point3D getLocation() {
        return location;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String toString(){
        return "{ Location=" + location + ", Reference=" + page +
                ", ClusteringKeyValue=" + objClusteringKeyValue + " }";
    }
}
