package BP.domain.dao;

import BP.domain.entity.Student;
import BP.domain.entity.StudentSiblings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiblingRelationshipRepo extends JpaRepository<StudentSiblings, Integer> {
    void deleteByStudent(Student student);
    void deleteBySibling(Student sibling);
}

