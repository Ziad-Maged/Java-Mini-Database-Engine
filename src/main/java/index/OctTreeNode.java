package main.java.index;

import java.io.Serializable;

public class OctTreeNode implements Serializable {
    private OctTreeEntry[] entries;
    private OctTreeNode[] children;
    private boolean full;
    private Point3D centerPoint, minimum, maximum;
    private int size;
    public OctTreeNode(){
        entries = new OctTreeEntry[OctTree.getMaxEntriesInOctTreeNode()];
    }

    /**
     * In case the node is full, and we need to insert a new values.
     * We split the node into 8 nodes that are not full and redistribute the entries accordingly.*/
    public void split(){
        //TODO Later
        entries = null;
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public Point3D getCenterPoint() {
        return centerPoint;
    }

    public Point3D getMaximum() {
        return maximum;
    }

    public Point3D getMinimum(){
        return minimum;
    }

    public void setMinimum(Point3D minimum) {
        this.minimum = minimum;
    }

    public void setMaximum(Point3D maximum) {
        this.maximum = maximum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public OctTreeEntry get(int index){
        if(index >= entries.length || index < 0)
            return null;
        return entries[index];
    }

    public void insert(String pageName, Point3D location, Object objClusteringKeyValue){
        if(!full){
            entries[size++] = new OctTreeEntry(pageName, objClusteringKeyValue, location);
            if(size >= entries.length)
                full = true;
        }
        else{
            //TODO
            size++;
        }
    }
}
