package ro.utcn.ssatr.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reprezinta o persoana care viziteaza cladirea.
 */
public class Vizitator {

    private UUID id;
    private String nume;
    private String email;
    private TipVizitator tip;

    public Vizitator(String nume, String email, TipVizitator tip) {
        this.id = UUID.randomUUID();
        this.nume = nume;
        this.email = email;
        this.tip = tip;
    }

    public UUID getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getEmail() {
        return email;
    }

    public TipVizitator getTip() {
        return tip;
    }

    @Override
    public String toString() {
        return "Vizitator{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", email='" + email + '\'' +
                ", tip=" + tip +
                '}';
    }
}