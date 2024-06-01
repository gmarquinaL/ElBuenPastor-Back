package BP.application.service;

import BP.application.dto.StudentDTO;
import BP.application.dto.StudentSimpleDTO;
import BP.application.util.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IStudentService {
    ResponseEntity<GenericResponse<StudentDTO>> saveStudent(StudentDTO studentDTO) throws Exception;
    ResponseEntity<GenericResponse<List<StudentSimpleDTO>>> findAllSimple();
    ResponseEntity<GenericResponse<StudentDTO>> findDetailedById(Integer id) throws Exception;
    ResponseEntity<GenericResponse<StudentDTO>> updateStudent(StudentDTO studentDTO) throws Exception;
    ResponseEntity<GenericResponse<Void>> deleteStudent(Integer id) throws Exception;
    ResponseEntity<GenericResponse<StudentDTO>> assignGuardianToStudent(Integer studentId, Integer guardianId) throws Exception;
    ResponseEntity<GenericResponse<StudentDTO>> assignSiblingToStudent(Integer studentId, Integer siblingId) throws Exception;

    List<StudentDTO> searchStudents(String query);
}
