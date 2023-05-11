package main.java.index;

import java.io.Serializable;

public class Point3D implements Serializable {
    private final double x, y, z;

    public Point3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String toString(){
        return "(" + x + ", " + y + ", " + z +")";
    }
}
