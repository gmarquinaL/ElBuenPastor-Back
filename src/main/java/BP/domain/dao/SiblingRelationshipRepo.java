package BP.domain.dao;

import BP.domain.entity.StudentSiblings;
import org.springframework.stereotype.Repository;

@Repository
public interface SiblingRelationshipRepo extends IGenericRepo<StudentSiblings, Integer> {
}
