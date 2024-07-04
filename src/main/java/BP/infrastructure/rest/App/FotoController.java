package BP.infrastructure.rest.App;

import BP.application.service.impl.App.FotoService;
import BP.application.util.BestGenericResponse;
import BP.domain.entity.App.Foto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("foto")
public class FotoController {
    private final FotoService service;

    public FotoController(FotoService service) {
        this.service = service;
    }

    @GetMapping
    public BestGenericResponse<Iterable<Foto>> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public BestGenericResponse<Foto> find(@PathVariable Long id) {
        return service.find(id);
    }

    @GetMapping("/download/filename/{fileName}")
    public ResponseEntity<Resource> downloadByFileName(@PathVariable String fileName, HttpServletRequest request) {
        return service.downloadByFileName(fileName, request);
    }
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id, HttpServletRequest request) {
        return service.downloadById(id, request);
    }
    @PostMapping
    public BestGenericResponse<Foto> save(@ModelAttribute Foto obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public BestGenericResponse<Foto> update(@PathVariable Long id, @ModelAttribute Foto obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public BestGenericResponse<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
