package main.java.index;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class OctTree implements Serializable {
    private String strIndexName;
    private OctTreeNode root;
    private double minX, maxX, minY, maxY, minZ, maxZ;
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
        root = new OctTreeNode(maxEntriesInOctTreeNode);
        minX = htblColNameRanges.get("MinX");
        maxX = htblColNameRanges.get("MaxX");
        minY = htblColNameRanges.get("MinY");
        maxY = htblColNameRanges.get("MaxY");
        minZ = htblColNameRanges.get("MinZ");
        maxZ = htblColNameRanges.get("MaxZ");
    }

    public String getStrIndexName() {
        return strIndexName;
    }

    public void insert(String strClusteringKey, Hashtable<String, Object> htblColNameValues){
        //TODO Later
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
