package main;
import exceptions.DBAppException;
import exceptions.InvalidInputException;

import java.io.*;
import java.util.*;
public class Table implements Serializable{

    private String tableName;
    private Vector<PageDetails> details;
    private int numberOfRecords;
    private int numberOfPages;

    private transient boolean inserting;

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

    private int binarySearch(String strClustringKey, Hashtable<String, Object> record,
                             Vector<Hashtable<String, Object>> page) throws InvalidInputException {
        int start = 0;
        int end = page.size() - 1;
        int middle = (start + end) / 2;
        while(start < end){
            int comparison = compareWith(record.get(strClustringKey), page.get(middle).get(strClustringKey));
            if(comparison == 1){
                start = middle + 1;
            }else if(comparison == -1){
                end = middle;
            }else if(comparison == 0){
                if(inserting)
                    throw new InvalidInputException("Clustring Key already exists");
                else
                    return middle;
            }
            middle = (start + end) / 2;
        }
        return middle;
    }

    public void insert(String strClustringKey, Hashtable<String,Object> htblColNameValue) throws InvalidInputException {
        inserting = true; // to indicate that the table is currently inserting a value
        boolean shift = false; // boolean variable to indicate whether we need to shift or not
        Page p = null; // preparing a Page variable
        /*
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
                if(compareMin < 0 && compareMax < 0){ // if input record is < the minimum and maximum records of the page
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
                    p.savePage(".\\" + DBApp.getStrCurrentDatabaseName()); // saving the page after the insertion process
                }else if(compareMin > 0 && compareMax < 0){ // checking if the input belongs to the current page
                    p = loadPage(".\\" + DBApp.getStrCurrentDatabaseName() +
                            "\\" + e.getPageName() + ".class"); //load the current page
                    p.setName(e.getPageName()); // setting the name of the page
                    int indexOfInsertion = binarySearch(strClustringKey, htblColNameValue, p.getRecords()); // get the index of insertion using binary search
                    if(e.isFull()){ // checking if the page is already full
                        shift = true; // setting the shift to true to start the shifting process
                        temp = p.getRecords().get(p.getRecords().size() - 1); // setting the temp variable to be the maximum record of the page to shift it to the next page
                        p.getRecords().remove(temp); // removing the last record from the page to maintain the maximum number of records per page
                    }
                    p.getRecords().insertElementAt(htblColNameValue, indexOfInsertion); // inserting the element in the page at the specified index
                    e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // resetting the maximum record of the page to be the current last record of that page
                    if(p.isFull()) // checking if the page was not full before insertion and became full after insertion
                        e.setFull(true); // if true then we indicate that the page is now full and any further insertions in that page require shifting
                    p.savePage(".\\" + DBApp.getStrCurrentDatabaseName()); // saving the page after the insertion process
                }else if(compareMin > 0 && compareMax > 0){ // checking if the input is greater than the maximum
                    if(e.isFull()) // if the page is already full, we skip this iteration and restart the loop
                        continue; // skipping the iteration
                    else { // if the page is not full
                        int indexOfNextPage = details.indexOf(e) + 1; // finding the index of the next page
                        if(!(indexOfNextPage >= details.size())){ // checking if the index of the next page is greater than the number of pages in the table, then the current page is the last page
                            p = loadPage(".\\" + DBApp.getStrCurrentDatabaseName() +
                                    "\\" + e.getPageName() + ".class"); //load the current page
                            p.setName(e.getPageName()); // setting the name of the page
                            p.getRecords().add(htblColNameValue); // inserting the input in the page
                            e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // updating the maximum since the input is greater than the maximum
                            if(p.isFull()) // checking if the page is full after the insertion process
                                e.setFull(true); // updating the status of the page after the insertion process makes the page full
                            p.savePage(".\\" + DBApp.getStrCurrentDatabaseName()); // saving the page after the insertion process
                            break; // exiting out of the loop
                        }else
                            indexOfNextPage--; // in case the current page is the last page.
                        int compareMin2 = compareWith(htblColNameValue.get(strClustringKey), details.get(indexOfNextPage).getMinimumRecord().get(strClustringKey)); // comparing the input with the minimum record of the next page
                        if(compareMin2 > 0){ // checking if the input is greater than the maximum of the current page but less than the minimum of the next page
                            p = loadPage(".\\" + DBApp.getStrCurrentDatabaseName() +
                                    "\\" + e.getPageName() + ".class"); //load the current page
                            p.setName(e.getPageName()); // setting the name of the page
                            p.getRecords().add(htblColNameValue); // inserting the input in the page
                            e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // updating the maximum since the input is greater than the maximum
                            if(p.isFull()) // checking if the page is full after the insertion process
                                e.setFull(true); // updating the status of the page after the insertion process makes the page full
                            p.savePage(".\\" + DBApp.getStrCurrentDatabaseName()); // saving the page after the insertion process
                            break; // exiting out of the loop
                        }else // if it is not the case that the input is less than the minimum of the next page
                            continue; // we skip the iteration because the input is greater than the minimum of the next page
                    }
                }
            }else { // if shifting must be done.
                if(!e.isFull()){ // if the page in question is not full then the shifting stops here
                    p = loadPage(".\\" + DBApp.getStrCurrentDatabaseName() +
                            "\\" + e.getPageName() + ".class"); //load the current page
                    p.setName(e.getPageName()); // setting the name of the page
                    p.getRecords().insertElementAt(temp, 0); // inserting the maximum of the previous page as the minimum in the next page
                    e.setMinimumRecord(p.getRecords().get(0)); // updating the minimum of the current Page Detail in question
                    if(p.isFull()) // checking if the page is full after insertion
                        e.setFull(true); // updating the page's details to be true the next time we check if it is full
                    p.savePage(".\\" + DBApp.getStrCurrentDatabaseName()); // saving the page after the insertion process
                    temp = null; // setting the temp to null to check if the last page was full or not.
                    break; // exiting out of the loop immediately after
                }else { // if the page in question is also empty, same process with minor differences
                    p = loadPage(".\\" + DBApp.getStrCurrentDatabaseName() +
                            "\\" + e.getPageName() + ".class"); //load the current page
                    p.setName(e.getPageName()); // setting the name of the page
                    p.getRecords().insertElementAt(temp, 0); // inserting the maximum of the previous page as the minimum in the next page
                    e.setMinimumRecord(p.getRecords().get(0)); // updating the minimum of the current Page Detail in question
                    temp = p.getRecords().get(p.getRecords().size() - 1); // updating the temp to be the maximum of the current page in question to continue the shifting process
                    p.getRecords().remove(p.getRecords().size() - 1); // removing the old maximum in the page
                    e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // updating the detail of the page by setting the maximum to the current maimum in the page
                    p.savePage(".\\" + DBApp.getStrCurrentDatabaseName()); // saving the page after the insertion process
                }
            }
        }
        if(temp != null){ // if the temp is not null then the last page was also full, and we need to continue insertion
            numberOfPages++; // incrementing the number of pages by one
            p = new Page(tableName, numberOfPages); // creating the new page
            p.getRecords().add(temp); // inserting the maximum of the last page in the table as the minimum in the new page
            addNewPage(".\\" + DBApp.getStrCurrentDatabaseName(), p); // adding the new page to the table's details and saving it
        }
        numberOfRecords++; // incrementing the number of records in the table after each successful insertion
        this.saveTable(".\\" + DBApp.getStrCurrentDatabaseName() +
                "\\" + tableName + ".class"); // saving the details of the table after insertion
    }

    public void delete(String strClusteringKeyValue, Hashtable<String,Object> htblColNameValue){
        //TODO LATER
    }

    public void update(String strClustringKey, Object objClusteringKeyValue,
                       Hashtable<String,Object> htblColNameValue) throws DBAppException {
        Page p; // preparing a temporary page
        for(PageDetails e : details){ // looping over the details of the page to compare the min and max values of the clustering key
            int compareMin = compareWith(objClusteringKeyValue,
                    e.getMinimumRecord().get(strClustringKey)); // comparing with the minimum in the current page
            int compareMax = compareWith(objClusteringKeyValue,
                    e.getMaximumRecord().get(strClustringKey)); // comparing with the maximum of the current page
            if(compareMin >= 0 && compareMax <= 0){ // checking if the record in question is in the current page
                p = loadPage(".\\" + DBApp.getStrCurrentDatabaseName() +
                        "\\" + e.getPageName() + ".class"); // loading the current page
                p.setName(e.getPageName()); // setting the current page name to be able to save later
                if(compareMin == 0){ // checking if the record in question is the minimum record (The first record)
                    for(String s : p.getRecords().get(0).keySet()){ // looping over all the keys in the record if the condition is true
                        if(!s.equals(strClustringKey)) // checking that the current key is not the clustering key
                            p.getRecords().get(0).put(s, htblColNameValue.get(s)); // updating the content of the record
                    }
                    e.setMinimumRecord(p.getRecords().get(0)); // updating the minimum record of the details of the page to be able to save
                    p.savePage(".\\" + DBApp.getStrCurrentDatabaseName()); // saving the current page
                    return; // exiting out of the method entirely.
                }else if(compareMax == 0){ // checking if the record in question is the maximum record
                    for(String s : p.getRecords().get(p.getRecords().size() - 1).keySet()){ // looping over all the keys in the record if the condition is true
                        if(!s.equals(strClustringKey)) // checking that the current key is not the clustering key
                            p.getRecords().get(p.getRecords().size() - 1).put(s, htblColNameValue.get(s)); // updating the content of the record
                    }
                    e.setMaximumRecord(p.getRecords().get(p.getRecords().size() - 1)); // updating the maximum record of the details of the page to be able to save
                    p.savePage(".\\" + DBApp.getStrCurrentDatabaseName()); // saving the page
                    return; // exiting out of the method entirely
                }else { // if it is somewhere in the middle of the page
                    Hashtable<String, Object> temp = new Hashtable<>(); // creating a temp hashtable for the binary search operation
                    temp.put(strClustringKey, objClusteringKeyValue); // adding the clustering key and its value to the temp
                    int index = binarySearch(strClustringKey, temp, p.getRecords()); // performing binary search on the page to find the record in question
                    if(compareWith(p.getRecords().get(index).get(strClustringKey), objClusteringKeyValue) != 0) // checking if the record in question is present in the table
                        throw new InvalidInputException("Input value for clustering key could not be found"); // halting the program and throwing an exception if the record in question is not present in the table if the condition is true
                    for(String s : p.getRecords().get(index).keySet()){ // looping over all the keys in the record if the condition is true
                        if(!s.equals(strClustringKey)) // checking that the current key is not the clustering key
                            p.getRecords().get(index).put(s, htblColNameValue.get(s)); // updating the content of the record
                    }
                    p.savePage(".\\" + DBApp.getStrCurrentDatabaseName()); // saving the page
                    return; // exiting out of the method entirely
                }
            }
        }
    }

    public String toString(){
        StringBuilder result = new StringBuilder(tableName + ":\n");
        for(PageDetails e : details){
            Page p = loadPage(".\\" + DBApp.getStrCurrentDatabaseName() +
                    "\\" + e.getPageName() + ".class");
            p.setId(e.getPageID());
            result.append(p.toString()).append("\n");
        }
        return result.toString();
    }
}
