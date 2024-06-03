package BP.application.service.impl;

import BP.application.dto.StudentDTO;
import BP.application.dto.StudentSimpleDTO;
import BP.application.util.GenericResponse;
import BP.domain.dao.IGuardianRepo;
import BP.domain.dao.IStudentRepo;
import BP.domain.dao.SiblingRelationshipRepo;
import BP.domain.dao.StudentSiblingsRepo;
import BP.domain.entity.Guardian;
import BP.domain.entity.StudentSiblings;
import BP.domain.entity.Student;
import BP.application.service.IStudentService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements IStudentService {

    private final IStudentRepo studentRepo;
    private final IGuardianRepo guardianRepo;
    private final SiblingRelationshipRepo siblingRelationshipRepo;
    private final ModelMapper modelMapper;  // Inyectado a través del constructor gracias a Lombok
    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);
    @Autowired
    private StudentSiblingsRepo studentSiblingsRepo;
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

            // Manejar la relación de hermanos
            if (studentDTO.getSiblings() != null && !studentDTO.getSiblings().isEmpty()) {
                for (StudentDTO siblingDTO : studentDTO.getSiblings()) {
                    Student sibling = studentRepo.findById(siblingDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Sibling not found with ID: " + siblingDTO.getId()));

                    StudentSiblings studentSiblings = new StudentSiblings(student, sibling);
                    student.getStudentSiblings().add(studentSiblings);

                    StudentSiblings reverseStudentSiblings = new StudentSiblings(sibling, student);
                    sibling.getStudentSiblings().add(reverseStudentSiblings);

                    studentSiblingsRepo.save(studentSiblings);
                    studentSiblingsRepo.save(reverseStudentSiblings);
                }
            }

            student = studentRepo.save(student);  // Guarda el estudiante con las relaciones

            StudentDTO savedStudentDTO = modelMapper.map(student, StudentDTO.class);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Student added successfully", savedStudentDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to add student: " + e.getMessage(), null));
        }
    }


    @Transactional
    @Override
    public ResponseEntity<GenericResponse<List<StudentSimpleDTO>>> findAllSimple() {
        try {
            List<Student> students = studentRepo.findAll();
            List<StudentSimpleDTO> studentSimpleDTOs = students.stream()
                    .map(student -> {
                        // Determina el estado de hermanos verificando la tabla intermedia
                        boolean hasSiblings = !student.getStudentSiblings().isEmpty();
                        String siblingStatus = hasSiblings ? "Tiene hermanos" : "No tiene hermanos";

                        return new StudentSimpleDTO(
                                student.getId(),
                                student.getFullName(),
                                student.getGuardian() != null ? student.getGuardian().getFullName() : "Sin apoderado",
                                siblingStatus
                        );
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Students retrieved successfully", studentSimpleDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to retrieve students: " + e.getMessage(), null));
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

                // Mapeo de los hermanos utilizando la tabla intermedia
                List<StudentDTO> siblings = student.getStudentSiblings().stream()
                        .map(StudentSiblings::getSibling)
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
    public ResponseEntity<GenericResponse<StudentDTO>> updateStudent(StudentDTO studentDTO) throws Exception {
        try {
            Student student = studentRepo.findById(studentDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentDTO.getId()));

            // Actualizar datos del estudiante
            student.setFullName(studentDTO.getFullName());
            student.setLevel(studentDTO.getLevel());
            student.setSection(studentDTO.getSection());
            student.setGrade(studentDTO.getGrade());
            student.setCurrent(studentDTO.isCurrent());
            student.setGender(studentDTO.getGender());

            // Actualizar o crear guardián
            if (studentDTO.getGuardian() != null) {
                Guardian guardian = guardianRepo.findById(studentDTO.getGuardian().getId())
                        .orElseThrow(() -> new RuntimeException("Guardian not found with ID: " + studentDTO.getGuardian().getId()));
                guardian.setFullName(studentDTO.getGuardian().getFullName());
                guardian.setLivesWithStudent(studentDTO.getGuardian().isLivesWithStudent());
                guardianRepo.save(guardian);
                student.setGuardian(guardian);
            }

            // Manejar hermanos
            handleSiblingRelationships(student, studentDTO.getSiblings());

            studentRepo.save(student);

            // Mapear el estudiante actualizado a StudentDTO
            StudentDTO updatedStudentDTO = modelMapper.map(student, StudentDTO.class);
            updatedStudentDTO.setSiblings(studentDTO.getSiblings()); // Actualiza con los últimos hermanos

            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Student updated successfully", updatedStudentDTO));
        } catch (Exception e) {
            throw new RuntimeException("Failed to update student: " + e.getMessage(), e);
        }
    }

    private void handleSiblingRelationships(Student student, List<StudentDTO> siblingDTOs) throws Exception {
        // Eliminar todas las relaciones de hermanos existentes
        siblingRelationshipRepo.deleteByStudent(student);
        siblingRelationshipRepo.deleteBySibling(student);

        // Crear nuevas relaciones de hermanos si existen en el DTO
        if (siblingDTOs != null && !siblingDTOs.isEmpty()) {
            for (StudentDTO siblingDTO : siblingDTOs) {
                if (siblingDTO.getId() == null) {
                    throw new RuntimeException("Sibling ID must not be null");
                }
                Student sibling = studentRepo.findById(siblingDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Sibling not found with ID: " + siblingDTO.getId()));
                // Crear la relación del estudiante a su hermano
                StudentSiblings newRelationship = new StudentSiblings(student, sibling);
                siblingRelationshipRepo.save(newRelationship);
                // Crear la relación inversa del hermano al estudiante
                StudentSiblings reverseRelationship = new StudentSiblings(sibling, student);
                siblingRelationshipRepo.save(reverseRelationship);
            }
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

            // Verifica si ya existe la relación de hermanos antes de agregarla
            boolean alreadyExists = student.getStudentSiblings().stream()
                    .anyMatch(rel -> rel.getSibling().equals(sibling));

            if (!alreadyExists) {
                StudentSiblings relationship = new StudentSiblings();
                relationship.setStudent(student);
                relationship.setSibling(sibling);
                siblingRelationshipRepo.save(relationship);

                // Crear la relación inversa
                StudentSiblings reverseRelationship = new StudentSiblings();
                reverseRelationship.setStudent(sibling);
                reverseRelationship.setSibling(student);
                siblingRelationshipRepo.save(reverseRelationship);
            }

            StudentDTO studentDTO = modelMapper.map(student, StudentDTO.class);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Sibling assigned to student successfully", studentDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to assign sibling to student: " + e.getMessage(), null));
        }
    }

    @Override
    public List<StudentDTO> searchStudents(String name) {
        List<Student> students = studentRepo.searchByName(name);
        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }
}
