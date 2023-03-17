package exceptions;

public class TableDoesNotExistException extends Exception {

    public TableDoesNotExistException(){
        super();
    }

    public TableDoesNotExistException(String message){
        super(message);
    }

}
