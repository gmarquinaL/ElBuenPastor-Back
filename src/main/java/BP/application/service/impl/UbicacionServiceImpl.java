package BP.application.service.impl;


import BP.application.util.BestGenericResponse;
import BP.application.util.Global;
import BP.domain.dao.UbicacionRepo;
import BP.domain.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UbicacionServiceImpl {

    @Autowired
    private UbicacionRepo ubicacionRepository;

    // Agregar una nueva ubicación
    public BestGenericResponse<Location> agregarUbicacion(Location ubicacion) {
        try {
            Location savedLocation = ubicacionRepository.save(ubicacion);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Ubicación agregada correctamente", savedLocation);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al agregar la ubicación", null);
        }
    }

    // Editar una ubicación existente
    public BestGenericResponse<Location> editarUbicacion(Location ubicacion) {
        if (!ubicacionRepository.existsById(ubicacion.getId())) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Ubicación no encontrada", null);
        }
        try {
            Location updatedLocation = ubicacionRepository.save(ubicacion);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Ubicación actualizada con éxito", updatedLocation);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al actualizar la ubicación", null);
        }
    }

    // Eliminar una ubicación
    public BestGenericResponse<Void> eliminarUbicacion(Integer id) {
        if (!ubicacionRepository.existsById(id)) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Ubicación no encontrada", null);
        }
        try {
            ubicacionRepository.deleteById(id);
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Ubicación eliminada correctamente", null);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Error al eliminar la ubicación", null);
        }
    }

    // Listar todas las ubicaciones
    public BestGenericResponse<List<Location>> listarTodasLasUbicaciones() {
        try {
            List<Location> ubicaciones = new ArrayList<>();
            ubicacionRepository.findAll().forEach(ubicaciones::add);
            return new BestGenericResponse<>(Global.TIPO_DATA, Global.RPTA_OK, Global.OPERACION_CORRECTA, ubicaciones);
        } catch (Exception e) {
            return new BestGenericResponse<>(Global.TIPO_RESULT, Global.RPTA_ERROR, Global.OPERACION_ERRONEA, null);
        }
    }

    // Obtener información de una ubicación específica
    public BestGenericResponse<Location> obtenerInformacionUbicacion(Integer id) {
        Optional<Location> ubicacion = ubicacionRepository.findById(id);
        if (ubicacion.isPresent()) {
            return new BestGenericResponse<>(Global.TIPO_CORRECTO, Global.RPTA_OK, "Información de la ubicación obtenida", ubicacion.get());
        } else {
            return new BestGenericResponse<>(Global.TIPO_ERROR, Global.RPTA_ERROR, "Ubicación no encontrada", null);
        }
    }
}
