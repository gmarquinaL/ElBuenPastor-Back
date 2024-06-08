package BP.application.exception;



import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenericExceptionHandler{
    @ExceptionHandler(Exception.class)
    public BestGenericResponse genericException(Exception ex) {
        return new BestGenericResponse("exception", -1, Global.OPERACION_ERRONEA, ex.getMessage());
    }
}
