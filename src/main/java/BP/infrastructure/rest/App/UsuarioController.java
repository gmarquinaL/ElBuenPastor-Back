package BP.infrastructure.rest.App;


import BP.application.service.impl.App.UsuarioService;
import BP.application.util.BestGenericResponse;
import BP.domain.entity.App.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping("/registro")
    public BestGenericResponse<Member> register(@RequestBody Member member) {
        return service.register(member);
    }

    @PostMapping("/login")
    public BestGenericResponse<Member> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        return service.login(email, password);
    }
}