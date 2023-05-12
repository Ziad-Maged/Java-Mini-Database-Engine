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
        children = new OctTreeNode[8];
        for(int i = 0; i < children.length; i++)
            children[i] = new OctTreeNode();
        centerPoint = new Point3D((minimum.x() + maximum.x()) / 2,
                minimum.y() + maximum.y() / 2,
                minimum.z() + maximum.z() / 2);

        //First Octant
        children[0].setMinimum(centerPoint);
        children[0].setMaximum(maximum);
        //Second Octant
        children[1].setMinimum(new Point3D(minimum.x(), centerPoint.y(), centerPoint.z()));
        children[1].setMaximum(new Point3D(centerPoint.x(), maximum.y(), maximum.z()));
        //Third Octant
        children[2].setMinimum(new Point3D(minimum.x(), minimum.y(), centerPoint.z()));
        children[2].setMaximum(new Point3D(centerPoint.x(), centerPoint.y(), maximum.z()));
        //Fourth Octant
        children[3].setMinimum(new Point3D(centerPoint.x(), minimum.y(), centerPoint.z()));
        children[3].setMaximum(new Point3D(maximum.x(), centerPoint.y(), maximum.z()));
        //Fifth Octant
        children[4].setMinimum(new Point3D(centerPoint.x(), centerPoint.y(), minimum.z()));
        children[4].setMaximum(new Point3D(maximum.x(), maximum.y(), centerPoint.z()));
        //Sixth Octant
        children[5].setMinimum(new Point3D(minimum.x(), centerPoint.y(), minimum.z()));
        children[5].setMaximum(new Point3D(centerPoint.x(), maximum.y(), centerPoint.z()));
        //Seventh Octant
        children[6].setMinimum(minimum);
        children[6].setMaximum(centerPoint);
        //Eighth Octant
        children[7].setMinimum(new Point3D(centerPoint.x(), minimum.y(), minimum.z()));
        children[7].setMaximum(new Point3D(maximum.x(), centerPoint.y(), centerPoint.z()));
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
            this.split();
            size++;
        }
    }
}
