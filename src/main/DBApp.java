package main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.opencsv.CSVWriter;

import java.io.*;
import exceptions.*;

public class DBApp {

    private static String strCurrentDatabaseName;

    public void init() throws IOException {
        createDatabase("Database");
        selectDatabase("Database");
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

    public static void selectDatabase(String strDatabaseName) {
        strCurrentDatabaseName = strDatabaseName;
    }

    public void createTable(String strTableName, String strClusteringKeyColumn,
                            Hashtable<String, String> htblColNameType,
                            Hashtable<String, String> htblColNameMin,
                            Hashtable<String, String> htblColNameMax) throws DBAppException{
        for(String e : htblColNameType.values()) {
            if(!e.equals("java.lang.Integer") && !e.equals("java.lang.Double") &&
                    !e.equals("java.util.Date") && !e.equals("java.lang.String")) {
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
            Table table = new Table(strTableName);
            table.saveTable(".\\" + strCurrentDatabaseName);
        }catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void createIndex(String strTableName, String[] strarrColName) throws DBAppException{
        //TODO IN MILESTONE TWO
    }

    private void checkMinMaxInput(Hashtable<String, Object> htblColNameValue,
                                  Hashtable<String, String> htblColNameMin,
                                  Hashtable<String, String> htblColNameMax) throws DBAppException{
        for(String e : htblColNameValue.keySet()){
            if(htblColNameValue.get(e) instanceof Integer &&
                    ((Integer)htblColNameValue.get(e)).compareTo(Integer.parseInt(htblColNameMin.get(e))) < 0
            && ((Integer)htblColNameValue.get(e)).compareTo(Integer.parseInt(htblColNameMin.get(e))) > 0){
                throw new InvalidInputException("The input value is less than the minimum or greater than the maximum");
            }else if(htblColNameValue.get(e) instanceof Double &&
                    ((Double)htblColNameValue.get(e)).compareTo(Double.parseDouble(htblColNameMin.get(e))) < 0
                    && ((Double)htblColNameValue.get(e)).compareTo(Double.parseDouble(htblColNameMin.get(e))) > 0){
                throw new InvalidInputException("The input value is less than the minimum or greater than the maximum");
            }else if(htblColNameValue.get(e) instanceof String &&
                    ((String)htblColNameValue.get(e)).compareTo(htblColNameMin.get(e)) < 0
                    && ((String)htblColNameValue.get(e)).compareTo(htblColNameMin.get(e)) > 0){
                throw new InvalidInputException("The input value is less than the minimum or greater than the maximum");
            }else if(htblColNameValue.get(e) instanceof Date){
                try{
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Date inputDate = (Date) htblColNameValue.get(e);
                    Date minDate = formatter.parse(htblColNameMin.get(e));
                    Date maxDate = formatter.parse(htblColNameMax.get(e));
                    if(inputDate.compareTo(minDate) < 0 && inputDate.compareTo(maxDate) > 0)
                        throw new InvalidInputException();
                }catch(Exception e1){
                    throw new InvalidInputException("Date not entered correctly");
                }
            }
        }
    }

    private Object[] getTableDetails(String strTableName,
                                     Hashtable<String, Object> htblColNameValue,
                                     String strClustringKeyValue) throws DBAppException, IOException {
        Object[] result = new Object[4];
        Hashtable<String, String> htblColNameType = new Hashtable<>();
        Hashtable<String, String> htblColNameMin = new Hashtable<>();
        Hashtable<String, String> htblColNameMax = new Hashtable<>();
        String strClustringKey = "";
        Object objClustringKeyValue = strClustringKeyValue;
        BufferedReader br = new BufferedReader(new FileReader(".\\" + strCurrentDatabaseName +
                "\\metadata.csv"));
        String s = br.readLine();
        while(s != null){
            String[] header = s.split(",");
            if(header[0].equals(strTableName)){
                htblColNameType.put(header[1], header[2]);
                htblColNameMin.put(header[1], header[6]);
                htblColNameMax.put(header[1], header[7]);
                if(header[3].equals("True"))
                    strClustringKey = header[1];
            }
            s = br.readLine();
        }
        if(htblColNameType.isEmpty())
            throw new TableDoesNotExistException(strTableName  + " table does not exist");
        for(String e : htblColNameType.keySet()){
            if((htblColNameType.get(e).equals("java.lang.Integer") &&
                    !(htblColNameValue.get(e) instanceof Integer)) ||
                    (htblColNameType.get(e).equals("java.lang.Double") &&
                            !(htblColNameValue.get(e) instanceof Double)) ||
                    (htblColNameType.get(e).equals("java.lang.String") &&
                            !(htblColNameValue.get(e) instanceof String)) ||
                    (htblColNameType.get(e).equals("java.util.Date") &&
                            !(htblColNameValue.get(e) instanceof Date)))
                throw new TypeMissMatchException(e + " is of type " + htblColNameType.get(e));
        }
        if(strClustringKeyValue != null){
            try{
                if(htblColNameType.get(strClustringKey).equals("java.lang.Integer"))
                    objClustringKeyValue = Integer.parseInt(strClustringKeyValue);
                else if(htblColNameType.get(strClustringKey).equals("java.lang.Double"))
                    objClustringKeyValue = Double.parseDouble(strClustringKeyValue);
                else if(htblColNameType.get(strClustringKey).equals("java.util.Date")){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        objClustringKeyValue = formatter.parse(strClustringKeyValue);
                    } catch (ParseException e) {
                        throw new InvalidInputException();
                    }
                }
            }catch(ClassCastException | InvalidInputException e){
                throw new InvalidInputException("Input data does not match the type of the clustering key");
            }
        }
        result[0] = strClustringKey;
        result[1] = htblColNameMin;
        result[2] = htblColNameMax;
        result[3] = objClustringKeyValue;
        return result;
    }

    public void insertIntoTable(String strTableName,
                                Hashtable<String, Object> htblColNameValue) throws DBAppException, IOException, ClassNotFoundException {
        Object[] parameters = getTableDetails(strTableName, htblColNameValue, null);
        checkMinMaxInput(htblColNameValue, (Hashtable<String, String>) parameters[1],
                (Hashtable<String, String>) parameters[2]);
        Table table;
        FileInputStream fileIn = new FileInputStream(".\\" + strCurrentDatabaseName +
                "\\" + strTableName + ".class");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        table = (Table) in.readObject();
        fileIn.close();
        in.close();
        if(table.getNumberOfRecords() == 0 && table.getNumberOfPages() == 0){
            table.setNumberOfPages(1);
            table.setNumberOfRecords(1);
            Page page = new Page(table.getTableName(), table.getNumberOfPages());
            page.getRecords().add(htblColNameValue);
            table.addNewPage(".\\" + strCurrentDatabaseName, page);
        }else{
            table.insert(parameters[0].toString(), htblColNameValue);
        }
        table.saveTable(".\\" + strCurrentDatabaseName);
    }

    public void updateTable(String strTableName,
                            String strClusteringKeyValue,
                            Hashtable<String,Object> htblColNameValue) throws DBAppException, IOException, ClassNotFoundException {
        if(strClusteringKeyValue == null || strClusteringKeyValue.equals(""))
            throw new InvalidInputException("Clustring Key value should not be empty");
        Object[] parameters = getTableDetails(strTableName, htblColNameValue, strClusteringKeyValue);
        checkMinMaxInput(htblColNameValue, (Hashtable<String, String>) parameters[1],
                (Hashtable<String, String>) parameters[2]);
        Table table;
        FileInputStream fileIn = new FileInputStream(".\\" + strCurrentDatabaseName +
                "\\" + strTableName + ".class");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        table = (Table) in.readObject();
        fileIn.close();
        in.close();
        table.update(parameters[0].toString(), parameters[3], htblColNameValue);
        table.saveTable(".\\" + strCurrentDatabaseName);
    }

    public void deleteFromTable(String strTableName, Hashtable<String,Object> htblColNameValue) throws DBAppException{
        //TODO LATER
    }

    public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws DBAppException{
        //TODO LATER
        return null;
    }

    public static String getStrCurrentDatabaseName() {
        return strCurrentDatabaseName;
    }


    public static void main(String[] args) throws Exception {
        DBApp app = new DBApp();
        app.init();
        Hashtable<String, String> htblColNameType = new Hashtable<>();
        Hashtable<String, String> htblColNameMin = new Hashtable<>();
        Hashtable<String, String> htblColNameMax = new Hashtable<>();
        htblColNameType.put("id", "java.lang.Integer");
        htblColNameMin.put("id", "1");
        htblColNameMax.put("id", "1000");
        app.createTable("Test1", "id", htblColNameType, htblColNameMin, htblColNameMax);

        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put("id", 1);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 10);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 5);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 7);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 50);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 28);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 22);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 81);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 20);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 21);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 80);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 30);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 23);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 24);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();
        htblColNameValue.put("id", 33);
        app.insertIntoTable("Test1", htblColNameValue);
        htblColNameValue.clear();

        FileInputStream fileIn = new FileInputStream(".\\" + strCurrentDatabaseName + "\\Test1.class");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        Table t = (Table) in.readObject();
        fileIn.close();
        in.close();
        System.out.println(t);


    }

}
