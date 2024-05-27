package BP.application.util;

import java.util.HashMap;
import java.util.Map;

public class GenericResponse<T> {
    private String status;
    private int code;
    private String message;
    private T data;
    private Map<String, Object> additionalInfo = new HashMap<>();

    public GenericResponse() {
    }

    public GenericResponse(String status, int code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, Object> getAdditionalInfo() {
        return additionalInfo;
    }

    public void addInfo(String key, Object value) {
        this.additionalInfo.put(key, value);
    }
}
