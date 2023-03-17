package exceptions;

public class TableAlreadyExistsException extends Exception {

    public TableAlreadyExistsException(){
        super();
    }

    public TableAlreadyExistsException(String message){
        super(message);
    }

}
