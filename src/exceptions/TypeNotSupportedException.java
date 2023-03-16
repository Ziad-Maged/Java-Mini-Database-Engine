package exceptions;

public class TypeNotSupportedException extends DBAppException{

    public TypeNotSupportedException(){
        super();
    }

    public TypeNotSupportedException(String message){
        super(message);
    }

}
