package BP.infrastructure.rest;

import BP.application.dto.StudentDTO;
import BP.application.dto.StudentSimpleDTO;
import BP.application.service.IStudentService;
import BP.application.util.GenericResponse;
import org.springframework.http.HttpStatus;
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
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteStudent(@PathVariable Integer id) throws Exception {
        return studentService.deleteStudent(id);
    }
    @GetMapping("/simple")
    public ResponseEntity<GenericResponse<List<StudentSimpleDTO>>> getAllStudentsSimple() {
        return studentService.getAllStudentsSimple();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<StudentDTO>> getStudentDetails(@PathVariable Integer id) throws Exception {
        return studentService.getStudentDetails(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GenericResponse<StudentDTO>> updateStudent(@PathVariable Integer id, @RequestBody StudentDTO studentDTO) {
        try {
            studentDTO.setId(id);
            return studentService.updateStudent(studentDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>("error", -1, "Failed to update student: " + e.getMessage(), null));
        }
    }

    @PostMapping("/assignGuardian/{studentId}/{guardianId}")
    public ResponseEntity<GenericResponse<Void>> assignGuardianToStudent(@PathVariable Integer studentId, @PathVariable Integer guardianId) throws Exception {
        return studentService.assignGuardianToStudent(studentId, guardianId);
    }

    @PostMapping("/assignSibling/{studentId}/{siblingId}")
    public ResponseEntity<GenericResponse<StudentDTO>> assignSiblingToStudent(@PathVariable Integer studentId, @PathVariable Integer siblingId) throws Exception {
        return studentService.assignSiblingToStudent(studentId, siblingId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentDTO>> searchStudents(@RequestParam String query) {
        List<StudentDTO> students = studentService.searchStudents(query);
        return ResponseEntity.ok(students);
    }
}
