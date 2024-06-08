package BP.application.service.impl;


import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.dao.AdministrativoRepo;
import BP.domain.entity.Administrative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministrativoServiceImpl {

    @Autowired
    private AdministrativoRepo administrativoRepo;

    // Agregar un nuevo empleado administrativo
    public BestGenericResponse<Administrative> agregarAdministrativo(Administrative administrativo) {
        try {
            Administrative savedAdministrativo = administrativoRepo.save(administrativo);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Administrativo agregado correctamente", savedAdministrativo);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al agregar administrativo", null);
        }
    }

    // Editar un empleado administrativo existente
    public BestGenericResponse<Administrative> editarAdministrativo(Administrative administrativo) {
        if (!administrativoRepo.existsById(administrativo.getId())) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Administrativo no encontrado", null);
        }
        try {
            Administrative updatedAdministrativo = administrativoRepo.save(administrativo);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Administrativo actualizado correctamente", updatedAdministrativo);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al actualizar administrativo", null);
        }
    }

    // Eliminar un empleado administrativo
    public BestGenericResponse<Void> eliminarAdministrativo(Integer id) {
        if (!administrativoRepo.existsById(id)) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Administrativo no encontrado", null);
        }
        try {
            administrativoRepo.deleteById(id);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Administrativo eliminado correctamente", null);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al eliminar administrativo", null);
        }
    }

    // Listar todos los empleados administrativos
    public BestGenericResponse<List<Administrative>> listarTodosLosAdministrativos() {
        try {
            List<Administrative> administrativos = administrativoRepo.findAll();
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Lista de administrativos obtenida correctamente", administrativos);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al obtener la lista de administrativos", null);
        }
    }
}
