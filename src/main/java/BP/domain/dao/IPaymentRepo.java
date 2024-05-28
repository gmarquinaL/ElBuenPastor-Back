package BP.domain.dao;

import BP.domain.entity.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPaymentRepo extends IGenericRepo<Payment, Long> {
    // Buscar pagos por nombre de usuario
    List<Payment> findByName(String name);

    // Buscar pagos por código
    List<Payment> findByCode(String code);

    // Buscar pagos por concepto
    List<Payment> findByConcept(String concept);

    // Buscar pagos por fecha de pago
    List<Payment> findByPaymentDate(LocalDateTime paymentDate);

    // Obtener códigos únicos
    @Query("SELECT DISTINCT p.code FROM Payment p")
    List<String> findDistinctCode();

    // Obtener conceptos únicos
    @Query("SELECT DISTINCT p.concept FROM Payment p")
    List<String> findDistinctConcept();

    // Obtener fechas de pago únicas
    @Query("SELECT DISTINCT p.paymentDate FROM Payment p")
    List<LocalDateTime> findDistinctPaymentDate();

    // Obtener nombres únicos
    @Query("SELECT DISTINCT p.name FROM Payment p")
    List<String> findDistinctName();

    // Verificar si existe un pago duplicado basándose en todos los campos relevantes
    List<Payment> findByAgencyAndCodeAndConceptAndReferenceDocAndPaymentDateAndDueDateAndPaymentMethodAndAmountAndNameAndUsername(
            String agency, String code, String concept, String referenceDoc, LocalDateTime paymentDate,
            LocalDate dueDate, String paymentMethod, BigDecimal amount, String name, String username);

    // Método para filtrar pagos por nombre, fecha de inicio y fecha de fin
    @Query("SELECT p FROM Payment p WHERE (:name IS NULL OR p.name = :name) AND (:startDate IS NULL OR p.paymentDate >= :startDate) AND (:endDate IS NULL OR p.paymentDate <= :endDate)")
    List<Payment> findByFilters(@Param("name") String name, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}
