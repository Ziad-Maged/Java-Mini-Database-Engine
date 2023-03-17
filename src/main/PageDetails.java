package main;

import java.util.Hashtable;

public class PageDetails {

    private String pageName;
    private int pageID;
    private Hashtable<String, Object> minimumRecord;
    private Hashtable<String, Object> maximumRecord;

    public PageDetails(String pageName, int pageID, Hashtable<String, Object> minimumRecord,
                       Hashtable<String, Object> maximumRecord){
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

    public Hashtable<String, Object> getMinimumRecord(){
        return minimumRecord;
    }

    public void setMinimumRecord(Hashtable<String, Object> minimumRecord){
        this.minimumRecord = minimumRecord;
    }

    public Hashtable<String, Object> getMaximumRecord(){
        return maximumRecord;
    }

    public void setMaximumRecord(Hashtable<String, Object> maximumRecord){
        this.maximumRecord = maximumRecord;
    }

}
