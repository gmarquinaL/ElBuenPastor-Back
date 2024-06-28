package BP.application.util.archiveutilexception;


import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import org.hibernate.JDBCException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static BP.application.util.Global.*;


@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpecificExceptionHandler {
    @ExceptionHandler(JDBCException.class)
    public BestGenericResponse sqlException(JDBCException ex) {
        return new BestGenericResponse("sql-exception", -1, OPERACION_ERRONEA, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BestGenericResponse validException(MethodArgumentNotValidException ex) {
        return new BestGenericResponse("valid-exception", RPTA_ERROR, OPERACION_ERRONEA, ex.getMessage());
    }

    @ExceptionHandler(FileStorageException.class)
    public BestGenericResponse fileStorageException(FileStorageException ex) {
        return new BestGenericResponse("file-storage-exception", RPTA_ERROR, OPERACION_ERRONEA, ex.getMessage());
    }

    @ExceptionHandler(MyFileNotFoundException.class)
    public BestGenericResponse myFileNotFoundException(MyFileNotFoundException exception) {
        return new BestGenericResponse("my-file-not-found-exception", RPTA_ERROR, OPERACION_INCORRECTA, exception.getMessage());
    }
}