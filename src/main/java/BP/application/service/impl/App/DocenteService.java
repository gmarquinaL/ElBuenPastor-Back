package BP.application.service.impl.App;


import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.dao.App.DocenteRepository;
import BP.domain.entity.App.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocenteService {

    @Autowired
    private DocenteRepository docenteRepository;

    // Punto 1: Agregar un docente
    public BestGenericResponse<Teacher> agregarDocente(Teacher docente) {
        try {
            Teacher savedDocente = docenteRepository.save(docente);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Docente agregado correctamente", savedDocente);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al agregar docente", null);
        }
    }

    // Punto 1: Editar un docente
    public BestGenericResponse<Teacher> editarDocente(Teacher docente) {
        if (!docenteRepository.existsById(docente.getId())) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Docente no encontrado", null);
        }
        try {
            Teacher updatedDocente = docenteRepository.save(docente); // save() actualiza si el id existe
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Docente actualizado correctamente", updatedDocente);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al actualizar docente", null);
        }
    }

    // Punto 1: Eliminar un docente
    public BestGenericResponse<Void> eliminarDocente(Integer id) {
        if (!docenteRepository.existsById(id)) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Docente no encontrado", null);
        }
        try {
            docenteRepository.deleteById(id);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Docente eliminado correctamente", null);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al eliminar docente", null);
        }
    }

    // Punto 1: Listar todos los docentes
    public BestGenericResponse<List<Teacher>> listarTodosLosDocentes() {
        try {
            List<Teacher> docentes = docenteRepository.findAll();
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Lista de docentes obtenida correctamente", docentes);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener lista de docentes", null);
        }
    }
    // Obtener detalles de un docente específico
    public BestGenericResponse<Teacher> obtenerDocentePorId(Integer id) {
        try {
            return docenteRepository.findById(id)
                    .map(docente -> new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Docente encontrado", docente))
                    .orElse(new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Docente no encontrado", null));
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al buscar al docente", null);
        }
    }

}
