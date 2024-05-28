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

            Student student = modelMapper.map(studentDTO, Student.class);  // Mapea DTO a Entidad
            student.setGuardian(guardian);  // Asigna el guardián al estudiante
            student = studentRepo.save(student);  // Guarda el estudiante

            StudentDTO savedStudentDTO = modelMapper.map(student, StudentDTO.class);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Student added successfully", savedStudentDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to add student: " + e.getMessage(), null));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<GenericResponse<List<StudentSimpleDTO>>> findAllSimple() {
        try {
            List<StudentSimpleDTO> students = studentRepo.findAllSimple();
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Successfully retrieved all students", students));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Error retrieving students: " + e.getMessage(), null));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<GenericResponse<StudentDTO>> findDetailedById(Integer id) {
        try {
            Optional<Student> optionalStudent = studentRepo.findById(id);
            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);

                // Mapeo de los hermanos
                List<StudentDTO> siblings = student.getSiblings().stream()
                        .map(sibling -> modelMapper.map(sibling, StudentDTO.class))
                        .collect(Collectors.toList());
                studentDTO.setSiblings(siblings);

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
    public ResponseEntity<GenericResponse<StudentDTO>> updateStudent(StudentDTO studentDTO) {
        try {
            Student existingStudent = studentRepo.findById(studentDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            modelMapper.map(studentDTO, existingStudent);  // Mapea los datos del DTO a la entidad existente

            if (studentDTO.getGuardian() != null) {
                Guardian guardian = modelMapper.map(studentDTO.getGuardian(), Guardian.class);
                existingStudent.setGuardian(guardianRepo.save(guardian));
            }

            studentRepo.save(existingStudent);
            StudentDTO updatedStudentDTO = modelMapper.map(existingStudent, StudentDTO.class);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Student updated successfully", updatedStudentDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to update student: " + e.getMessage(), null));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<GenericResponse<Void>> deleteStudent(Integer id) {
        try {
            studentRepo.deleteById(id);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Student deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to delete student: " + e.getMessage(), null));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<GenericResponse<StudentDTO>> assignGuardianToStudent(Integer studentId, Integer guardianId) {
        try {
            Student student = studentRepo.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            Guardian guardian = guardianRepo.findById(guardianId)
                    .orElseThrow(() -> new RuntimeException("Guardian not found"));

            student.setGuardian(guardian);
            studentRepo.save(student);

            StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Guardian assigned to student successfully", studentDTO));
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

            student.addSibling(sibling);
            studentRepo.save(student);

            StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Sibling assigned to student successfully", studentDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to assign sibling to student: " + e.getMessage(), null));
        }
    }

}
