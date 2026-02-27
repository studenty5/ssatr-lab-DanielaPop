package ro.utcn.ssatr.visitor_system_web.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ro.utcn.ssatr.visitor_system_web.model.Vizita;
import ro.utcn.ssatr.visitor_system_web.repository.VizitaRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitExpirationService {

    private final VizitaRepository repository;

    public VisitExpirationService(VizitaRepository repository) {
        this.repository = repository;
    }

    // Rulează la fiecare 60 secunde
    @Scheduled(fixedRate = 10000)
    public void expireVisits() {

        List<Vizita> programate = repository.findByStatus("PROGRAMATA");

        for (Vizita vizita : programate) {

            if (vizita.getExpirationTime() != null &&
                    vizita.getExpirationTime().isBefore(LocalDateTime.now())) {

                vizita.setStatus("EXPIRATA");
                repository.save(vizita);

                System.out.println("Vizita expirata automat: " + vizita.getVisitorName());
            }
        }
    }
}