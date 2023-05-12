package main.java.index;

import java.io.Serializable;

public record Point3D(double x, double y, double z) implements Serializable {

    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public boolean equals(Point3D point) {
        return x == point.x() && y == point.y() && z == point.z();
    }
}
