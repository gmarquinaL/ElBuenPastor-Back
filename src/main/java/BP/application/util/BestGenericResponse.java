package BP.application.util;

public class BestGenericResponse<T> {
    private String type;
    private int rpta;
    private String message;
    private T body;

    public BestGenericResponse() {
        this.type = "";
        this.rpta = 0;
        this.message = "";
        this.body = null;
    }

    public BestGenericResponse(String type, int rpta, String message, T body) {
        this.type = type;
        this.rpta = rpta;
        this.message = message;
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRpta() {
        return rpta;
    }

    public void setRpta(int rpta) {
        this.rpta = rpta;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
