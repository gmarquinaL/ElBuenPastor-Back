package BP.application.service.impl.App;


import BP.application.dto.App.TeacherDTO;
import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.dao.App.DocenteRepository;
import BP.domain.dao.App.UsuarioRepository;
import BP.domain.entity.App.Member;
import BP.domain.entity.App.Teacher;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocenteService {

    @Autowired
    private DocenteRepository docenteRepository;
    @Autowired
    private UsuarioRepository memberRepository;
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
    @Transactional
    public BestGenericResponse<Teacher> editarDocente(Teacher docente) {
        if (!docenteRepository.existsById(docente.getId())) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Docente no encontrado", null);
        }
        try {
            // Actualizar la información del docente
            Teacher updatedDocente = docenteRepository.save(docente);

            // Actualizar la información del miembro asociado
            Member member = memberRepository.findByTeacherId(docente.getId());
            if (member != null) {
                member.setEmail(docente.getEmail());
                member.setValidity(docente.isActive());
                memberRepository.save(member);
            }

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

    // Listar todos los docentes usando DTOs
    public BestGenericResponse<List<TeacherDTO>> listarTodosLosDocentes() {
        try {
            List<Teacher> docentes = docenteRepository.findAll();
            List<TeacherDTO> docentesDTO = docentes.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Lista de docentes obtenida correctamente", docentesDTO);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener lista de docentes", null);
        }
    }
    // Obtener detalles de un docente específico usando DTO
    public BestGenericResponse<TeacherDTO> obtenerDocentePorId(Integer id) {
        try {
            return docenteRepository.findById(id)
                    .map(docente -> new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Docente encontrado", convertToDTO(docente)))
                    .orElse(new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Docente no encontrado", null));
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al buscar al docente", null);
        }
    }

    // Convertir entidad a DTO
    private TeacherDTO convertToDTO(Teacher docente) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(docente.getId());
        dto.setFullName(docente.getFullName());
        dto.setPosition(docente.getPosition());
        dto.setDni(docente.getDni());
        dto.setEmail(docente.getEmail());
        dto.setPhone(docente.getPhone());
        dto.setAddress(docente.getAddress());
        dto.setHiringDate(docente.getHiringDate());
        dto.setActive(docente.isActive());
        return dto;
    }
}
