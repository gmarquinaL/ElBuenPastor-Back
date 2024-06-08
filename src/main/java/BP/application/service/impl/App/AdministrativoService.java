package BP.application.service.impl.App;


import BP.application.dto.App.AdministrativeDTO;
import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.dao.App.AdministrativoRepository;
import BP.domain.entity.App.Administrative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdministrativoService {

    @Autowired
    private AdministrativoRepository administrativoRepository;

    // Agregar un nuevo empleado administrativo
    public BestGenericResponse<Administrative> agregarAdministrativo(Administrative administrativo) {
        try {
            Administrative savedAdministrativo = administrativoRepository.save(administrativo);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Administrativo agregado correctamente", savedAdministrativo);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al agregar administrativo", null);
        }
    }

    // Editar un empleado administrativo existente
    public BestGenericResponse<Administrative> editarAdministrativo(Administrative administrativo) {
        if (!administrativoRepository.existsById(administrativo.getId())) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Administrativo no encontrado", null);
        }
        try {
            Administrative updatedAdministrativo = administrativoRepository.save(administrativo);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Administrativo actualizado correctamente", updatedAdministrativo);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al actualizar administrativo", null);
        }
    }

    // Eliminar un empleado administrativo
    public BestGenericResponse<Void> eliminarAdministrativo(Integer id) {
        if (!administrativoRepository.existsById(id)) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Administrativo no encontrado", null);
        }
        try {
            administrativoRepository.deleteById(id);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Administrativo eliminado correctamente", null);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al eliminar administrativo", null);
        }
    }

    // Listar todos los empleados administrativos usando DTOs
    public BestGenericResponse<List<AdministrativeDTO>> listarTodosLosAdministrativos() {
        try {
            List<Administrative> administrativos = administrativoRepository.findAll();
            List<AdministrativeDTO> administrativosDTO = administrativos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Lista de administrativos obtenida correctamente", administrativosDTO);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener la lista de administrativos", null);
        }
    }

    // Convertir entidad a DTO
    private AdministrativeDTO convertToDTO(Administrative administrativo) {
        AdministrativeDTO dto = new AdministrativeDTO();
        dto.setId(administrativo.getId());
        dto.setFullName(administrativo.getFullName());
        dto.setPosition(administrativo.getPosition());
        dto.setDni(administrativo.getDni());
        dto.setEmail(administrativo.getEmail());
        dto.setPhone(administrativo.getPhone());
        dto.setAddress(administrativo.getAddress());
        dto.setHiringDate(administrativo.getHiringDate());
        return dto;
    }
}
