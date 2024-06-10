package BP.application.service.impl.App;

import BP.application.util.BestGenericResponse;
import BP.domain.dao.App.UsuarioRepository;
import BP.domain.entity.App.Member;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public BestGenericResponse<Member> register(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword())); // Encripta la contraseña
        Member savedMember = repository.save(member);
        return new BestGenericResponse<>("SUCCESS", 1, "Usuario registrado exitosamente.", savedMember);
    }

    public BestGenericResponse<Member> login(String email, String password) {
        Optional<Member> usuarioOpt = repository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Member member = usuarioOpt.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                if (member.isValidity()) {
                    // Agregar información del Teacher si es relevante
                    if (member.getTeacher() != null) {
                        return new BestGenericResponse<>("SUCCESS", 1, "Inicio de sesión exitoso. Bienvenido, Profesor " + member.getTeacher().getFullName() + ".", member);
                    } else {
                        return new BestGenericResponse<>("ERROR", 0, "Este usuario no está asociado a un profesor.", null);
                    }
                } else {
                    return new BestGenericResponse<>("ERROR", 0, "Cuenta inactiva.", null);
                }
            }
        }
        return new BestGenericResponse<>("ERROR", 0, "Credenciales incorrectas.", null);
    }
}
