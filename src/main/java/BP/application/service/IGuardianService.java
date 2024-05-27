package BP.application.service;

import BP.application.dto.GuardianDTO;
import BP.application.util.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IGuardianService {
    ResponseEntity<GenericResponse<GuardianDTO>> saveGuardian(GuardianDTO guardianDTO) throws Exception;
    ResponseEntity<GenericResponse<GuardianDTO>> updateGuardian(GuardianDTO guardianDTO) throws Exception;
    ResponseEntity<GenericResponse<Void>> deleteGuardian(Integer id) throws Exception;
    ResponseEntity<GenericResponse<List<GuardianDTO>>> findAllGuardians() throws Exception;
}
