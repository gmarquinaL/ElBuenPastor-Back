package BP.application.service.impl.App;

import BP.application.util.BestGenericResponse;
import BP.domain.dao.App.FotoRepository;
import BP.domain.entity.App.Foto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import static BP.application.util.Global.*;

@Service
@Transactional
public class FotoService {

    private final FotoRepository repo;
    private final FileStorageService storageService;

    public FotoService(FotoRepository repo, FileStorageService storageService) {
        this.repo = repo;
        this.storageService = storageService;
    }

    public BestGenericResponse<Iterable<Foto>> list() {
        return new BestGenericResponse<>(TIPO_RESULT, RPTA_OK, OPERACION_CORRECTA, repo.list());
    }

    public BestGenericResponse<Foto> find(Long id) {
        Optional<Foto> foto = repo.findById(id);
        return foto.map(value -> new BestGenericResponse<>(TIPO_DATA, RPTA_OK, OPERACION_CORRECTA, value))
                .orElseGet(() -> new BestGenericResponse<>(TIPO_ERROR, RPTA_ERROR, "Foto no encontrada", null));
    }

    public BestGenericResponse<Foto> save(Foto obj) {
        String fileName = repo.findById(obj.getId()).map(Foto::getFileName).orElse("");
        String originalFilename = obj.getFile().getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        fileName = storageService.storeFile(obj.getFile(), fileName);

        obj.setFileName(fileName);
        obj.setExtension(extension);

        return new BestGenericResponse<>(TIPO_DATA, RPTA_OK, OPERACION_CORRECTA, repo.save(obj));
    }

    public ResponseEntity<Resource> download(String completeFileName, HttpServletRequest request) {
        Resource resource = storageService.loadResource(completeFileName);
        String contentType;

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public ResponseEntity<Resource> downloadByFileName(String fileName, HttpServletRequest request) {
        Foto doc = repo.findByFileName(fileName).orElse(new Foto());
        return download(doc.getCompleteFileName(), request);
    }
    public ResponseEntity<Resource> downloadById(Long id, HttpServletRequest request) {
        Foto doc = repo.findById(id).orElse(new Foto());
        return download(doc.getCompleteFileName(), request);
    }
    public BestGenericResponse<Void> delete(Long id) {
        Optional<Foto> foto = repo.findById(id);
        if (foto.isPresent()) {
            Foto f = foto.get();
            f.setEstado("I");
            f.setEliminado(true);
            repo.save(f);
            return new BestGenericResponse<>(TIPO_RESULT, RPTA_OK, OPERACION_CORRECTA, null);
        } else {
            return new BestGenericResponse<>(TIPO_ERROR, RPTA_ERROR, "Foto no encontrada", null);
        }
    }

    public HashMap<String, Object> validate(Foto obj) {
        // Implement validation logic here
        return new HashMap<>();
    }
}
