package BP.domain.dao.App;

import BP.domain.entity.App.TeacherPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<TeacherPayment, Integer> {
    List<TeacherPayment> findByTeacherId(Integer teacherId);
}
