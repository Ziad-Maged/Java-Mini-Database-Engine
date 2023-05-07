package main.java.index;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class OctTree implements Serializable {
    private final String strIndexName;
    private OctTreeNode root;
    private Point3D minPoint, maxPoint;
    private static int maxEntriesInOctTreeNode;

    public OctTree(String strIndexName, Hashtable<String, Double>htblColNameRanges){
        this.strIndexName = strIndexName;
        try {
            FileReader reader = new FileReader("src/main/resources/DBApp.config");
            Properties p = new Properties();
            p.load(reader);
            maxEntriesInOctTreeNode = Integer.parseInt(p.getProperty("MaximumEntriesinOctreeNode"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        root = new OctTreeNode();
        minPoint = new Point3D(htblColNameRanges.get("MinX"), htblColNameRanges.get("MinY"),
                htblColNameRanges.get("MinZ"));
        maxPoint = new Point3D(htblColNameRanges.get("MaxX"), htblColNameRanges.get("MaxY"),
                htblColNameRanges.get("MaxZ"));
    }

    public String getStrIndexName() {
        return strIndexName;
    }

    public Point3D getMinPoint() {
        return minPoint;
    }

    public Point3D getMaxPoint() {
        return maxPoint;
    }

    public static int getMaxEntriesInOctTreeNode(){
        return maxEntriesInOctTreeNode;
    }

    public void insert(String strClusteringKey, String pageName, Hashtable<String, Object> htblColNameValues){
        String[] info = strIndexName.split("_");
        double x = enumerateObjects(htblColNameValues.get(info[1]));
        double y = enumerateObjects(htblColNameValues.get(info[2]));
        double z = enumerateObjects(htblColNameValues.get(info[3]));
        root.insert(pageName, new Point3D(x, y, z), htblColNameValues.get(strClusteringKey));
    }

    public void delete(){

    }

    public Iterator selectFromTable(){
        //TODO Later
        return null;
    }

    public double enumerateObjects(Object data){
        double result = 0.0;
        if(data instanceof Integer)
            result = ((Integer)data).doubleValue();
        else if(data instanceof Double)
            result = (Double) data;
        else if(data instanceof String temp){
            for(int i = 1; i <= temp.length(); i++){
                result += temp.charAt(i - 1) * i;
            }
        }else if(data instanceof Date temp){
            result += temp.getDate() + (temp.getMonth() + 1) * 2 + (temp.getYear() + 1900);
        }
        return result;
    }

    public void saveIndex(){
        try{
            FileOutputStream fileOut = new FileOutputStream("src/main/resources/data/" + strIndexName + ".class");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            fileOut.close();
            out.close();
        }catch(Exception ignored){
            //Ignore
        }
    }

}
