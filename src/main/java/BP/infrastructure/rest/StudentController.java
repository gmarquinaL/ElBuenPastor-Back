package BP.infrastructure.rest;

import BP.application.dto.StudentDTO;
import BP.application.dto.StudentSimpleDTO;
import BP.application.service.IStudentService;
import BP.application.util.GenericResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final IStudentService studentService;

    public StudentController(IStudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/add")
    public ResponseEntity<GenericResponse<StudentDTO>> addStudent(@RequestBody StudentDTO studentDTO) throws Exception {
        return studentService.saveStudent(studentDTO);
    }

    @GetMapping("/simple")
    public ResponseEntity<GenericResponse<List<StudentSimpleDTO>>> getAllStudentsSimple() {
        return studentService.findAllSimple();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<StudentDTO>> getStudentDetails(@PathVariable Integer id) throws Exception {
        return studentService.findDetailedById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GenericResponse<StudentDTO>> updateStudent(@PathVariable Integer id, @RequestBody StudentDTO studentDTO) throws Exception {
        studentDTO.setId(id);
        return studentService.updateStudent(studentDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteStudent(@PathVariable Integer id) throws Exception {
        return studentService.deleteStudent(id);
    }

    @PostMapping("/assignGuardian/{studentId}/{guardianId}")
    public ResponseEntity<GenericResponse<StudentDTO>> assignGuardianToStudent(@PathVariable Integer studentId, @PathVariable Integer guardianId) throws Exception {
        return studentService.assignGuardianToStudent(studentId, guardianId);
    }

    @PostMapping("/assignSibling/{studentId}/{siblingId}")
    public ResponseEntity<GenericResponse<StudentDTO>> assignSiblingToStudent(@PathVariable Integer studentId, @PathVariable Integer siblingId) throws Exception {
        return studentService.assignSiblingToStudent(studentId, siblingId);
    }
}
