package BP.infrastructure.rest;

import BP.domain.entity.User;
import BP.application.security.JwtRequest;
import BP.application.security.JwtResponse;
import BP.application.security.JwtTokenUtil;
import BP.application.security.JwtUserDetailsService;
import BP.application.service.ITokenService;
import BP.application.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final IUserService userService;
    private final JwtUserDetailsService userDetailsService;
    private final ITokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody JwtRequest jwtRequest) throws Exception {
        jwtRequest.setPassword(URLDecoder.decode(jwtRequest.getPassword(), StandardCharsets.UTF_8));
        jwtRequest.setUsername(URLDecoder.decode(jwtRequest.getUsername(), StandardCharsets.UTF_8));

        User userFound = getUserByUsername(jwtRequest.getUsername());

        if (userFound != null) {
            validate(jwtRequest.getPassword(), userFound.getPassword());
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        authenticate(jwtRequest.getUsername(), jwtRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        tokenService.revokeAllTokensByUser(userFound);
        tokenService.saveToken(userFound, token);

        return new ResponseEntity<>(new JwtResponse(token), HttpStatus.OK);
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private User getUserByUsername(String username) {
        return userService.findOneByUsername(username);
    }

    private static void validate(String passwordFromUser, String passwordFromDB) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(passwordFromUser, passwordFromDB)) {
            throw new UsernameNotFoundException("CONTRASEÑA INCORRECTA");
        }
    }
}
