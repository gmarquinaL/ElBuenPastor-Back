package BP.application.util;

public class ConstantUtil
{
    public enum Estado {
        A,
        I
    }

    public static final String NOT_BLANK = "EL CAMPO NO PUEDE ESTAR VACÍO";
    public static final String NOT_NULL = "EL CAMPO NO PUEDE SER NULO";
    public static final String USER_NOT_ENABLED = "EL USUARIO NO ESTÁ HABILITADO: ";
    public static final String RESOURCE_NOT_FOUND = "RECURSO NO ENCONTRADO: ";
    public static final String TOKEN_BEARER = "BEARER";
    public static final String TIPO_RESULT = "result";
    public static final String TIPO_DATA = "data";
    public static final String TIPO_AUTH = "auth";
    public static final int RPTA_OK = 1;
    public static final int RPTA_WARNING = 0;
    public static final int RPTA_ERROR = -1;
    public static final String OPERACION_CORRECTA = "Operación finalizada correctamente";
    public static final String OPERACION_INCORRECTA = "No se ha podido culminar la operación";
    public static final String OPERACION_ERRONEA = "Ha ocurrido un error al realizar la operación";
    public static final String AUTH_SECRET = "ServicioBuenPastor";

}
