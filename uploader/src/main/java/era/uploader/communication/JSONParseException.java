package era.uploader.communication;

public class JSONParseException extends RuntimeException {

    public JSONParseException(String message) {
        super(message);
    }

    public static JSONParseException unexpectedField(Class<?> modelClass, String unexpected) {
        return new JSONParseException("Cannot parse inputted"
                + modelClass.getName()
                + "\nUnexpected field:"
                + unexpected);
    }

    public static JSONParseException fieldNotSupplied(Class<?> modelClass, String field) {
        return new JSONParseException("Unable to parse JSON from client. We are missing a \""
                + field
                + "\" field for entity"
                + modelClass.getName());
    }

}

