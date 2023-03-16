package main;

import java.util.Hashtable;

public class PageDetails {

    private String pageName;
    private int pageID;
    private Hashtable<String, String> minimumRecord;
    private Hashtable<String, String> maximumRecord;

    public PageDetails(String pageName, int pageID, Hashtable<String, String> minimumRecord,
                       Hashtable<String, String> maximumRecord){
        this.pageName = pageName;
        this.pageID = pageID;
        this.minimumRecord = minimumRecord;
        this.maximumRecord = maximumRecord;
    }

    public String getPageName(){
        return pageName;
    }

    public int getPageID(){
        return pageID;
    }

    public Hashtable<String, String> getMinimumRecord(){
        return minimumRecord;
    }

    public void setMinimumRecord(Hashtable<String, String> minimumRecord){
        this.minimumRecord = minimumRecord;
    }

    public Hashtable<String, String> getMaximumRecord(){
        return maximumRecord;
    }

    public void setMaximumRecord(Hashtable<String, String> maximumRecord){
        this.maximumRecord = maximumRecord;
    }

}
