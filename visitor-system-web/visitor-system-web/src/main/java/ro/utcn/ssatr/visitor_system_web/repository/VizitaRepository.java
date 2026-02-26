package ro.utcn.ssatr.visitor_system_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.utcn.ssatr.visitor_system_web.model.Vizita;

import java.util.List;
import java.util.UUID;

public interface VizitaRepository extends JpaRepository<Vizita, UUID> {

    List<Vizita> findByStatus(String status);

    long countByStatus(String status);
}