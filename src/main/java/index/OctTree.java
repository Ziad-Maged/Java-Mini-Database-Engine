package main.java.index;

import java.io.FileReader;
import java.io.Serializable;
import java.util.Properties;

public class OctTree implements Serializable {
    String strIndexName;
    OctTreeNode root;

    public OctTree(String strIndexName){
        this.strIndexName = strIndexName;
        try {
            FileReader reader = new FileReader("src/main/resources/DBApp.config");
            Properties p = new Properties();
            p.load(reader);
            root = new OctTreeNode(Integer.parseInt(p.getProperty("MaximumEntriesinOctreeNode")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
