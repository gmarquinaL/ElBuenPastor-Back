package BP.application.service.impl;

import BP.application.dto.StudentDTO;
import BP.application.dto.StudentSimpleDTO;
import BP.application.util.GenericResponse;
import BP.domain.dao.IGuardianRepo;
import BP.domain.dao.IStudentRepo;
import BP.domain.entity.Guardian;
import BP.domain.entity.Student;
import BP.application.service.IStudentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements IStudentService {

    private final IStudentRepo studentRepo;
    private final IGuardianRepo guardianRepo;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public ResponseEntity<GenericResponse<StudentDTO>> saveStudent(StudentDTO studentDTO) {
        try {
            Guardian guardian = null;
            if (studentDTO.getGuardian() != null) {
                if (studentDTO.getGuardian().getId() != null) {
                    guardian = guardianRepo.findById(studentDTO.getGuardian().getId())
                            .orElseThrow(() -> new RuntimeException("Guardian not found with ID: " + studentDTO.getGuardian().getId()));
                } else {
                    guardian = modelMapper.map(studentDTO.getGuardian(), Guardian.class);
                    guardian = guardianRepo.save(guardian); // Guarda el nuevo guardián
                }
            }

            Student student = modelMapper.map(studentDTO, Student.class);
            student.setGuardian(guardian);
            student = studentRepo.save(student);

            StudentDTO savedStudentDTO = mapStudentToDTO(student, true);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Student added successfully", savedStudentDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to add student: " + e.getMessage(), null));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<GenericResponse<List<StudentSimpleDTO>>> getAllStudentsSimple() {
        try {
            List<Student> students = studentRepo.findAll();
            List<StudentSimpleDTO> studentSimpleDTOs = students.stream()
                    .map(student -> new StudentSimpleDTO(
                            student.getId(),
                            student.getFullName(),
                            student.getGuardian() != null ? student.getGuardian().getFullName() : "Sin apoderado",
                            student.getSiblings().isEmpty() ? "No tiene hermanos" : "Tiene hermanos"
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Students retrieved successfully", studentSimpleDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to retrieve students: " + e.getMessage(), null));
        }
    }
    @Transactional
    @Override
    public ResponseEntity<GenericResponse<Void>> deleteStudent(Integer id) {
        try {
            if (studentRepo.existsById(id)) {
                studentRepo.deleteById(id);
                return ResponseEntity.ok(new GenericResponse<>("success", 1, "Student deleted successfully", null));
            } else {
                return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Student not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>("error", -1, "Failed to delete student: " + e.getMessage(), null));
        }
    }
    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<GenericResponse<StudentDTO>> getStudentDetails(Integer id) {
        try {
            Optional<Student> optionalStudent = studentRepo.findById(id);
            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                StudentDTO studentDTO = mapStudentToDTO(student, true);

                return ResponseEntity.ok(new GenericResponse<>("success", 1, "Student details retrieved successfully", studentDTO));
            } else {
                return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Student not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Error retrieving student details: " + e.getMessage(), null));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<GenericResponse<StudentDTO>> updateStudent(StudentDTO studentDTO) throws Exception {
        try {
            Student student = studentRepo.findById(studentDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentDTO.getId()));

            student.setFullName(studentDTO.getFullName());
            student.setLevel(studentDTO.getLevel());
            student.setSection(studentDTO.getSection());
            student.setGrade(studentDTO.getGrade());
            student.setCurrent(studentDTO.isCurrent());
            student.setGender(studentDTO.getGender());

            if (studentDTO.getGuardian() != null) {
                Guardian guardian = guardianRepo.findById(studentDTO.getGuardian().getId())
                        .orElseThrow(() -> new RuntimeException("Guardian not found with ID: " + studentDTO.getGuardian().getId()));
                student.setGuardian(guardian);
            }

            student = studentRepo.save(student);
            StudentDTO updatedStudentDTO = mapStudentToDTO(student, true);

            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Student updated successfully", updatedStudentDTO));
        } catch (Exception e) {
            throw new RuntimeException("Failed to update student: " + e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<GenericResponse<Void>> assignGuardianToStudent(Integer studentId, Integer guardianId) {
        try {
            Student student = studentRepo.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Guardian guardian = guardianRepo.findById(guardianId)
                    .orElseThrow(() -> new RuntimeException("Guardian not found"));

            student.setGuardian(guardian);
            studentRepo.save(student);

            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Guardian assigned to student successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to assign guardian to student: " + e.getMessage(), null));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<GenericResponse<StudentDTO>> assignSiblingToStudent(Integer studentId, Integer siblingId) {
        try {
            Student student = studentRepo.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Student sibling = studentRepo.findById(siblingId)
                    .orElseThrow(() -> new RuntimeException("Sibling not found"));

            // Verifica si ambos estudiantes tienen el mismo apoderado
            if (student.getGuardian() != null && student.getGuardian().equals(sibling.getGuardian())) {
                StudentDTO studentDTO = mapStudentToDTO(student, true);
                return ResponseEntity.ok(new GenericResponse<>("success", 1, "Sibling relationship acknowledged", studentDTO));
            } else {
                throw new RuntimeException("Students do not share the same guardian");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to assign sibling to student: " + e.getMessage(), null));
        }
    }

    @Override
    public List<StudentDTO> searchStudents(String query) {
        List<Student> students = studentRepo.searchByName(query);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    private StudentDTO mapStudentToDTO(Student student, boolean includeSiblings) {
        StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);
        if (includeSiblings) {
            List<StudentDTO> siblings = student.getSiblings().stream()
                    .map(sibling -> {
                        StudentDTO siblingDTO = modelMapper.map(sibling, StudentDTO.class);
                        siblingDTO.setSiblings(null); // Evita la recursión infinita
                        return siblingDTO;
                    })
                    .collect(Collectors.toList());
            studentDTO.setSiblings(siblings);
        } else {
            studentDTO.setSiblings(null);
        }
        return studentDTO;
    }
}
