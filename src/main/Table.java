package main;
import java.io.*;
import java.util.*;
public class Table implements Serializable{

    private String tableName;
    private ArrayList<PageDetails> details; //TODO Ask TAs about it.
    private int numberOfRecords;
    private int numberOfPages;

    public Table(String tableName) {
        this.tableName = tableName;
        details = new ArrayList<>();
        numberOfPages = 0;
        numberOfRecords = 0;
    }

    public String getTableName() {
        return tableName;
    }

    public ArrayList<PageDetails> getContent() {
        return details;
    }

    public int getNumberOfRecords(){
        return numberOfRecords;
    }

    public int getNumberOfPages(){
        return numberOfPages;
    }

    public void addNewPage(Page page, String path){
        details.add(new PageDetails(page.getName(), ++numberOfPages, page.getRecords().get(0), null));
        page.savePage(path);
    }

    public void saveTable(String filePath){
        try{
            FileOutputStream fileOut = new FileOutputStream(filePath + "\\" + this.tableName + ".class");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            fileOut.close();
            out.close();
        }catch(Exception e){

        }
    }

    public Page loadPage(String filePath){
        Vector<Hashtable<String, Object>> result = null;
        try{
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            result = (Vector<Hashtable<String, Object>>) in.readObject();
            fileIn.close();
            in.close();
            return new Page(result);
        }catch (Exception e1){
            return null;
        }
    }

    public void insert(Hashtable<String,Object> htblColNameValue){

    }

    public void delete(String strClusteringKeyValue, Hashtable<String,Object> htblColNameValue){

    }

    public void update(String strClusteringKeyValue, Hashtable<String,Object> htblColNameValue){

    }
}
