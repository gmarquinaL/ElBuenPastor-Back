package BP.application.security;

import BP.application.service.ITokenService;
import BP.domain.entity.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutHandler {

    private final ITokenService service;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        jwtToken = authHeader.substring(7);

        Token storedToken = service.findByToken(jwtToken);

        if(storedToken != null){
            service.deleteByUserIdAndToken(storedToken.getUser().getId(), jwtToken);
            SecurityContextHolder.clearContext();
        }
    }
}
