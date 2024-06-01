package BP.domain.dao;

import BP.application.dto.StudentSimpleDTO;
import BP.domain.entity.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IStudentRepo extends IGenericRepo<Student, Integer> {

    @Query("SELECT new BP.application.dto.StudentSimpleDTO(s.id, s.fullName, " +
            "(CASE WHEN g IS NULL THEN 'No tiene apoderado' ELSE g.fullName END), " +
            "'No tiene hermano') " +
            "FROM Student s " +
            "LEFT JOIN s.guardian g")
    List<StudentSimpleDTO> findAllSimple();

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.guardian")
    List<Student> findAllDetailed();


    @Query("SELECT s FROM Student s WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%',:name,'%'))")
    List<Student> searchByName(@Param("name") String name);

}
