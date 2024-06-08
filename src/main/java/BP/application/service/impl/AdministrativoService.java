package BP.application.service.impl;


import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.dao.AdministrativoRepository;
import BP.domain.entity.Administrative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // Listar todos los empleados administrativos
    public BestGenericResponse<List<Administrative>> listarTodosLosAdministrativos() {
        try {
            List<Administrative> administrativos = administrativoRepository.findAll();
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Lista de administrativos obtenida correctamente", administrativos);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener la lista de administrativos", null);
        }
    }
}
