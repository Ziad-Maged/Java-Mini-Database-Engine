package main.java.index;

import java.io.Serializable;

public class OctTreeNode implements Serializable {
    private final OctTreeEntry[] entries;
    private OctTreeNode[] children;
    private boolean full;
    private Point3D centerPoint;
    private int size;
    public OctTreeNode(){
        entries = new OctTreeEntry[OctTree.getMaxEntriesInOctTreeNode()];
    }

    /**
     * In case the node is full, and we need to insert a new values.
     * We split the node into 8 nodes that are not full and redistribute the entries accordingly.*/
    public void split(){
        //TODO Later
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

    public OctTreeEntry get(int index){
        if(index >= entries.length || index < 0)
            return null;
        return entries[index];
    }

    public void insert(String pageName, Point3D location, Object objClusteringKeyValue){
        if(!full)
            entries[size++] = new OctTreeEntry(pageName, objClusteringKeyValue, location);
    }
}
