package cl.usach.tingeso.valoresservice.repositories;

import cl.usach.tingeso.valoresservice.entities.ValoresEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValoresRepository extends JpaRepository<ValoresEntity, String> {
}
