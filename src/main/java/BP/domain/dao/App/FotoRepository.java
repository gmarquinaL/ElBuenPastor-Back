package BP.domain.dao.App;

import BP.domain.entity.App.Foto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FotoRepository extends CrudRepository<Foto, Long> {
    @Query("SELECT f FROM Foto f WHERE f.estado = 'A' AND f.eliminado = false")
    Iterable<Foto> list();

    @Query("SELECT f FROM Foto f WHERE f.fileName = :fileName AND f.estado = 'A' AND f.eliminado = false")
    Optional<Foto> findByFileName(String fileName);
}