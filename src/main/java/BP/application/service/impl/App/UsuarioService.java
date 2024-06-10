package BP.application.service.impl.App;

import BP.application.dto.App.MemberDTO;
import BP.application.dto.App.TeacherDTO;
import BP.application.util.BestGenericResponse;
import BP.domain.dao.App.UsuarioRepository;
import BP.domain.entity.App.Member;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository repository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public BestGenericResponse<MemberDTO> register(Member member) {
        logger.info("Registrando usuario con email: {}", member.getEmail());
        member.setPassword(passwordEncoder.encode(member.getPassword())); // Encripta la contraseña
        Member savedMember = repository.save(member);
        return new BestGenericResponse<>("SUCCESS", 1, "Usuario registrado exitosamente.", convertToDTO(savedMember));
    }

    public BestGenericResponse<MemberDTO> login(String email, String password) {
        logger.info("Intentando iniciar sesión con email: {}", email);
        Optional<Member> usuarioOpt = repository.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Member member = usuarioOpt.get();
            if (passwordEncoder.matches(password, member.getPassword())) {
                if (member.isValidity()) {
                    logger.info("Inicio de sesión exitoso para el usuario: {}", email);
                    // Inicializar asociaciones necesarias si el miembro está asociado con un profesor
                    if (member.getTeacher() != null) {
                        Hibernate.initialize(member.getTeacher());
                        logger.info("El usuario está asociado con el profesor: {}", member.getTeacher().getFullName());
                    }
                    return new BestGenericResponse<>("SUCCESS", 1, "Inicio de sesión exitoso.", convertToDTO(member));
                } else {
                    logger.warn("La cuenta para el usuario {} está inactiva.", email);
                    return new BestGenericResponse<>("ERROR", 0, "Cuenta inactiva.", null);
                }
            } else {
                logger.warn("Contraseña incorrecta para el usuario: {}", email);
                return new BestGenericResponse<>("ERROR", 0, "Credenciales incorrectas.", null);
            }
        }
        logger.warn("No se encontró el usuario con el email: {}", email);
        return new BestGenericResponse<>("ERROR", 0, "Credenciales incorrectas.", null);
    }

    private MemberDTO convertToDTO(Member member) {
        TeacherDTO teacherDTO = null;
        if (member.getTeacher() != null) {
            teacherDTO = new TeacherDTO(
                    member.getTeacher().getId(),
                    member.getTeacher().getFullName(),
                    member.getTeacher().getPosition(),
                    member.getTeacher().getDni(),
                    member.getTeacher().getEmail(),
                    member.getTeacher().getPhone(),
                    member.getTeacher().getAddress(),
                    member.getTeacher().getHiringDate(),
                    member.getTeacher().isActive()
            );
        }
        return new MemberDTO(member.getId(), member.getEmail(), member.isValidity(), teacherDTO);
    }
}
