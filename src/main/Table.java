package main;
import java.io.*;
import java.util.*;
public class Table implements Serializable{

    private String tableName;
    private Vector<PageDetails> details; //TODO Ask TAs about it.
    private int numberOfRecords;
    private int numberOfPages;

    public Table(String tableName) {
        this.tableName = tableName;
        details = new Vector<>();
        numberOfPages = 0;
        numberOfRecords = 0;
    }

    public String getTableName() {
        return tableName;
    }

    public Vector<PageDetails> getDetails() {
        return details;
    }

    public int getNumberOfRecords(){
        return numberOfRecords;
    }

    public void setNumberOfRecords(int numberOfRecords){
        this.numberOfRecords = numberOfRecords;
    }

    public void setNumberOfPages(int numberOfPages){
        this.numberOfPages = numberOfPages;
    }

    public int getNumberOfPages(){
        return numberOfPages;
    }

    public void addNewPage(String path, Page page){
        details.add(new PageDetails(page.getName(), numberOfPages, page.getRecords().get(0), page.getRecords().get(0)));
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

    // compareWith(o, e) = 1 if o > e, = -1 if o < e, = 0 if o = e
    private int compareWith(Object o, Object e){
        if(o instanceof Integer && e instanceof Integer)
            return ((Integer)o).compareTo((Integer)e);
        else if(o instanceof Double && e instanceof Double)
            return ((Double)o).compareTo((Double)e);
        else if(o instanceof String && e instanceof String)
            return ((String)o).compareTo((String)e);
        else if(o instanceof Date && e instanceof Date)
            return ((Date)o).compareTo((Date)e);
        return 0;
    }

    public void insert(String strClustringKey, Hashtable<String,Object> htblColNameValue){
        boolean shift = false; // boolean variable to indicate whether we need to shift or not
        Page p = null; // preparing a Page varialbe
        /**
         * preparing a temp variable to change the min and max values of a page
         * if needed during the shifting process
         */
        Hashtable<String, Object> temp = null;
        for(PageDetails e : details){ //looping over the pageDetails vector of the table
            if(!shift){ // checking if no shift is needed
                int compareMin = compareWith(htblColNameValue.get(strClustringKey),
                        e.getMinimumRecord().get(strClustringKey)); // comparing the input record with the minimum record of the page
                int compareMax = compareWith(htblColNameValue.get(strClustringKey),
                        e.getMaximumRecord().get(strClustringKey)); // comparing the input record with the maximum record of the page
                if(compareMin == -1 && compareMax == -1){ // if input record is < the minimum and maximum records of the page
                    p = loadPage(".\\" + DBApp.getStrCurrentDatabaseName() +
                            "\\" + e.getPageName() + ".class"); //load the current page
                    p.setName(e.getPageName()); //set the current page name for later serialization
                    if(e.isFull()){ // checking if the page is already full
                        shift = true; // setting the shift to true to start the shifting process
                        temp = p.getRecords().get(p.getRecords().size() - 1); // setting the temp variable to be the maximum record of the page to shift it to the next page
                        p.getRecords().remove(temp); // removing the last record from the page to maintain the maximum number of records per page
                        e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // resetting the maximum record of the page to be the current last record of that page
                    }
                    p.getRecords().insertElementAt(htblColNameValue, 0); // inserting the new record at the beginning of the vector since it is smaller than the minimum record
                    e.setMinimumRecord(htblColNameValue); // resetting the minimum record to be the input record since it is now the smallest
                    if(p.isFull()) // checking if the page was not full before insertion and became full after insertion
                        e.setFull(true); // if true then we indicate that the page is now full and any further insertions in that page require shifting
                    p.savePage(".\\" + DBApp.getStrCurrentDatabaseName() +
                            "\\" + e.getPageName() + ".class"); // saving the page after the insertion process
                }else if(compareMin == 1 && compareMax == -1){
                    //TODO Binary Search to find the needed index of insertion
                }else if(compareMin == 1 && compareMax == 1){
                    //TODO check if the page is not full
                }
            }else {
                //TODO Shifting process here
            }
        }
        numberOfRecords++; // incrementing the number of records in the table after each successful insertion
        this.saveTable(".\\" + DBApp.getStrCurrentDatabaseName() +
                "\\" + tableName + ".class"); // saving the details of the table after insertion
    }

    public void delete(String strClusteringKeyValue, Hashtable<String,Object> htblColNameValue){
        //TODO LATER
    }

    public void update(String strClusteringKeyValue, Hashtable<String,Object> htblColNameValue){
        //TODO LATER
    }
}
