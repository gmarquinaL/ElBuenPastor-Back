package BP.application.service.impl;

import BP.application.dto.GuardianDTO;
import BP.application.service.IGuardianService;
import BP.application.util.GenericResponse;
import BP.domain.dao.IGenericRepo;
import BP.domain.dao.IGuardianRepo;
import BP.domain.entity.Guardian;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuardianServiceImpl extends CRUDImpl<Guardian, Integer> implements IGuardianService {

    private final IGuardianRepo guardianRepo;
    private final ModelMapper modelMapper;

    @Override
    protected IGenericRepo<Guardian, Integer> getRepo() {
        return guardianRepo;
    }

    @Transactional
    @Override
    public ResponseEntity<GenericResponse<GuardianDTO>> saveGuardian(GuardianDTO guardianDTO) {
        try {
            Guardian guardian = modelMapper.map(guardianDTO, Guardian.class);
            guardian = guardianRepo.save(guardian);
            GuardianDTO savedGuardianDTO = modelMapper.map(guardian, GuardianDTO.class);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Guardian saved successfully", savedGuardianDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Failed to save guardian: " + e.getMessage(), null));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<GenericResponse<List<GuardianDTO>>> findAllGuardians() {
        try {
            List<Guardian> guardians = guardianRepo.findAll();
            List<GuardianDTO> dtos = guardians.stream()
                    .map(guardian -> modelMapper.map(guardian, GuardianDTO.class))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Successfully retrieved all guardians", dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Error retrieving guardians: " + e.getMessage(), null));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<GenericResponse<GuardianDTO>> updateGuardian(GuardianDTO guardianDTO) {
        try {
            Guardian existingGuardian = guardianRepo.findById(guardianDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Guardian not found"));

            existingGuardian.setFullName(guardianDTO.getFullName());
            existingGuardian.setLivesWithStudent(guardianDTO.getLivesWithStudent());

            guardianRepo.save(existingGuardian);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Guardian updated successfully", modelMapper.map(existingGuardian, GuardianDTO.class)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Error updating guardian: " + e.getMessage(), null));
        }
    }

    @Transactional
    @Override
    public ResponseEntity<GenericResponse<Void>> deleteGuardian(Integer id) {
        try {
            guardianRepo.deleteById(id);
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Guardian deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", -1, "Error deleting guardian: " + e.getMessage(), null));
        }
    }


    public List<GuardianDTO> searchGuardians(String name) {
        List<Guardian> guardians = guardianRepo.searchByName(name);
        return guardians.stream()
                .map(guardian -> modelMapper.map(guardian, GuardianDTO.class))
                .collect(Collectors.toList());
    }

}
