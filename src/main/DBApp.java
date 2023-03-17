package main;

import java.util.*;

import com.opencsv.CSVWriter;

import java.io.*;
import exceptions.*;

public class DBApp {

    private String strCurrentDatabaseName;

    public void init() throws IOException {

    }
    //creating directory
    public void createDatabase(String strDatabaseName) {
        File temp = new File(strDatabaseName);
        if(!temp.mkdir())
            System.out.println("Database Already Exits");
        temp = new File(".\\" + strDatabaseName + "\\metadata.csv");
        try {
            if(!temp.createNewFile())
                System.out.println("File Already Exists");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            FileWriter outputFile = new FileWriter(temp);
            CSVWriter writer = new CSVWriter(outputFile);
            String[] header = {"Table Name", "Column Name", "Column Type", "ClusteringKey", "IndexName",
                    "IndexType", "min", "max"};
            writer.writeNext(header);
            writer.close();
        }catch(Exception e) {
            System.out.println(e);
        }
    }

    public void selectDatabase(String strDatabaseName) {
        this.strCurrentDatabaseName = strDatabaseName;
    }

    public void createTable(String strTableName, String strClusteringKeyColumn,
                            Hashtable<String, String> htblColNameType,
                            Hashtable<String, String> htblColNameMin,
                            Hashtable<String, String> htblColNameMax) throws DBAppException, TypeNotSupportedException {
        for(String e : htblColNameType.values()) {
            if(!e.equals("java.lang.Integer") && !e.equals("java.lang.Double") &&
                    !e.equals("java.lang.Date") && !e.equals("java.lang.String")) {
                throw new TypeNotSupportedException(e + " is not a supported type.");
            }
        }

        File temp = new File(".\\" + strCurrentDatabaseName + "\\metadata.csv");
        try {

            BufferedReader br = new BufferedReader(new FileReader(".\\" + strCurrentDatabaseName +
                    "\\metadata.csv"));
            List<String[]> currentMetaDataStringList = new ArrayList<String[]>();
            String s = br.readLine();
            while(s != null) {
                currentMetaDataStringList.add(s.split(","));
                if(s.contains(strTableName)) {
                    br.close();
                    throw new TableAlreadyExistsException("Cannot have two tables with the same name.");
                }
                s = br.readLine();
            }
            br.close();
            FileWriter outputFile = new FileWriter(temp);
            CSVWriter writer = new CSVWriter(outputFile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            writer.writeAll(currentMetaDataStringList);
            String[] header = {strTableName, "", "", "", "", "", "", ""};
            for(Map.Entry<String, String> e : htblColNameType.entrySet()) {
                header[1] = e.getKey();
                header[2] = e.getValue();
                if(e.getKey().equals(strClusteringKeyColumn))
                    header[3] = "True";
                else
                    header[3] = "False";
                header[4] = header[5] = "null";
                header[6] = htblColNameMin.get(e.getKey());
                header[7] = htblColNameMax.get(e.getKey());
                writer.writeNext(header);
            }
            writer.close();
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void createIndex(String strTableName, String[] strarrColName) throws DBAppException{
        //TODO LATER
    }

    public void insertIntoTable(String strTableName,
                                Hashtable<String, Object> htblColNameValue) throws DBAppException{
        //TODO LATER
    }

    public void updateTable(String strTableName,
                            String strClusteringKeyValue,
                            Hashtable<String,Object> htblColNameValue) throws DBAppException{
        //TODO LATER
    }

    public void deleteFromTable(String strTableName, Hashtable<String,Object> htblColNameValue) throws DBAppException{
        //TODO LATER
    }

    public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws DBAppException{
        //TODO LATER
        return null;
    }

    public String getStrCurrentDatabaseName() {
        return strCurrentDatabaseName;
    }

	public static void main(String[] args) {
		DBApp app = new DBApp();
		app.createDatabase("ThisTestRun");
		app.selectDatabase("ThisTestRun");
		System.out.println(app.getStrCurrentDatabaseName());

		Hashtable<String, String> htblColNameType = new Hashtable<>();
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.Double");
		htblColNameType.put("id", "java.lang.Integer");

		Hashtable<String, String> htblColNameMin = new Hashtable<>();
		htblColNameMin.put("name", "A");
		htblColNameMin.put("gpa", "0");
		htblColNameMin.put("id", "0");

		Hashtable<String, String> htblColNameMax = new Hashtable<>();
		htblColNameMax.put("name", "ZZZZZZZZZZZZZZZ");
		htblColNameMax.put("gpa", "10000000");
		htblColNameMax.put("id", "1000000");

		try {
			app.createTable("Table1", "id", htblColNameType, htblColNameMin, htblColNameMax);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//-----------------------
		htblColNameType.clear();
		htblColNameType.put("taker", "java.lang.String");
		htblColNameType.put("allowance", "java.lang.Double");
		htblColNameType.put("id", "java.lang.Integer");

		htblColNameMin.put("taker", "A");
		htblColNameMin.put("allowance", "0");
		htblColNameMin.put("id", "0");

		htblColNameMax.put("taker", "ZZZZZZZZZZZZZZZ");
		htblColNameMax.put("allowance", "10000000");
		htblColNameMax.put("id", "1000000");

		try {
			app.createTable("Table2", "id", htblColNameType, htblColNameMin, htblColNameMax);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//    public static void main(String[] args) {
//        Page page = new Page("test");
//    }

}
