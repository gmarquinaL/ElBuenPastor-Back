package BP.application.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl {

    public boolean hasAccess(String path) {
        // Siempre retorna true, permitiendo el acceso a todos los usuarios
        return true;
    }

}
