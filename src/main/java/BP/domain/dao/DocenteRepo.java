package BP.domain.dao;


import BP.domain.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocenteRepo extends JpaRepository<Teacher, Integer> {
    // Punto 1: Agregar un docente, editarlo, eliminarlo, listar todos los docentes
}
