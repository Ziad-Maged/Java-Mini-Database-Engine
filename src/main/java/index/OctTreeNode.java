package main.java.index;

import java.io.Serializable;

public class OctTreeNode implements Serializable {
    private final OctTreeEntry[] entries;
    private OctTreeNode[] children;
    private boolean full;
    private Point3D centerPoint;
    private int size;
    public OctTreeNode(int size){
        entries = new OctTreeEntry[size];
    }

    /**
     * In case the node is full, and we need to insert a new values.
     * We split the node into 8 nodes that are not full and redistribute the entries accordingly.*/
    public void split(){

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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
