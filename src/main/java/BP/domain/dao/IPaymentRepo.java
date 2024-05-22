package BP.domain.dao;

import BP.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IPaymentRepo extends JpaRepository<Payment, Long> {
    // 1. Filter by Name
    List<Payment> findByName(String name);

    // 2. Filter by Code
    List<Payment> findByCode(String code);

    // 3. Filter by Concept
    List<Payment> findByConcept(String concept);

    // 4. Filter by Payment Date
    List<Payment> findByPaymentDate(LocalDateTime paymentDate);

    // 5. Filter by Name and Code
    List<Payment> findByNameAndCode(String name, String code);

    // 6. Filter by Name, Code, and Concept
    List<Payment> findByNameAndCodeAndConcept(String name, String code, String concept);

    // 7. Filter by Name, Code, Concept, and Payment Date
    List<Payment> findByNameAndCodeAndConceptAndPaymentDate(String name, String code, String concept, LocalDateTime paymentDate);

    // 8. Filter by Code and Concept
    List<Payment> findByCodeAndConcept(String code, String concept);

    // 9. Filter by Concept and Payment Date
    List<Payment> findByConceptAndPaymentDate(String concept, LocalDateTime paymentDate);

    // 10. Filter by Code and Payment Date
    List<Payment> findByCodeAndPaymentDate(String code, LocalDateTime paymentDate);

    // Get distinct codes from the database
    @Query("SELECT DISTINCT p.code FROM Payment p")
    List<String> findDistinctCode();

    // Get distinct concepts from the database
    @Query("SELECT DISTINCT p.concept FROM Payment p")
    List<String> findDistinctConcept();

    // Get distinct payment dates from the database
    @Query("SELECT DISTINCT p.paymentDate FROM Payment p")
    List<LocalDateTime> findDistinctPaymentDate();

    // Get distinct names from the database
    @Query("SELECT DISTINCT p.name FROM Payment p")
    List<String> findDistinctName();
}
