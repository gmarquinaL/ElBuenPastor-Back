package BP.application.service;

import BP.application.dto.StudentDTO;
import BP.application.dto.StudentSimpleDTO;
import BP.application.util.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IStudentService {
    ResponseEntity<GenericResponse<StudentDTO>> saveStudent(StudentDTO studentDTO) throws Exception;
    ResponseEntity<GenericResponse<List<StudentSimpleDTO>>> getAllStudentsSimple();

    @Transactional
    ResponseEntity<GenericResponse<Void>> deleteStudent(Integer id);

    ResponseEntity<GenericResponse<StudentDTO>> getStudentDetails(Integer id) throws Exception;
    ResponseEntity<GenericResponse<StudentDTO>> updateStudent(StudentDTO studentDTO) throws Exception;
    ResponseEntity<GenericResponse<Void>> assignGuardianToStudent(Integer studentId, Integer guardianId) throws Exception;
    ResponseEntity<GenericResponse<StudentDTO>> assignSiblingToStudent(Integer studentId, Integer siblingId) throws Exception;
    List<StudentDTO> searchStudents(String query);
}
