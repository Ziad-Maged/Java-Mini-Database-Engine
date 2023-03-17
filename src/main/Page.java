package main;

import java.io.*;
import java.util.*;
public class Page{

    private String name;
    private static int MAX_RECORDS_PER_PAGE;
    private Vector<Hashtable<String, Object>> records;

    //For creating a new page
    public Page(String name) {
        FileReader reader = null;
        try {
            reader = new FileReader(".\\resources\\DBApp.config");
            Properties p = new Properties();
            p.load(reader);

            MAX_RECORDS_PER_PAGE = Integer.parseInt(p.getProperty("MaximumRowsCountinTablePage"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.name = name;
        records = new Vector<Hashtable<String, Object>>(MAX_RECORDS_PER_PAGE);
    }

    //For loading a preexisting page
    public Page(Vector<Hashtable<String, Object>> records){
        this.records = records;
        FileReader reader = null;
        try {
            reader = new FileReader(".\\resources\\DBApp.config");
            Properties p = new Properties();
            p.load(reader);

            MAX_RECORDS_PER_PAGE = Integer.parseInt(p.getProperty("MaximumRowsCountinTablePage"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public static int getMaxRecordsPerPage() {
        return MAX_RECORDS_PER_PAGE;
    }

    public Vector<Hashtable<String, Object>> getRecords(){
        return records;
    }

    public boolean isFull() {
        return records.size() == MAX_RECORDS_PER_PAGE;
    }

    public void savePage(String path) {
        try{
            FileOutputStream fileOut = new FileOutputStream(path + "\\" + name);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(records);
            fileOut.close();
            out.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}