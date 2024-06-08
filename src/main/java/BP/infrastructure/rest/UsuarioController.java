package BP.infrastructure.rest;


import BP.application.service.impl.UsuarioServiceImpl;
import BP.application.util.BestGenericResponse;
import BP.domain.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioServiceImpl service;

    @PostMapping("/registro")
    public BestGenericResponse<Member> register(@RequestBody Member member) {
        return service.register(member);
    }

    @PostMapping("/login")
    public BestGenericResponse<Member> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        return service.login(email, password);
    }
}
