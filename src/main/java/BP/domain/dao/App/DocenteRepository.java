package BP.domain.dao.App;


import BP.domain.entity.App.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocenteRepository extends JpaRepository<Teacher, Integer> {
    // Punto 1: Agregar un docente, editarlo, eliminarlo, listar todos los docentes
}
