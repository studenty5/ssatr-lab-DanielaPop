package ro.utcn.ssatr.model;

import java.util.UUID;

/**
 * Reprezinta angajatul care primeste vizitatorul.
 */
public class Gazda {

    private UUID id;
    private String nume;
    private String departament;

    public Gazda(String nume, String departament) {
        this.id = UUID.randomUUID();
        this.nume = nume;
        this.departament = departament;
    }

    public UUID getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getDepartament() {
        return departament;
    }

    @Override
    public String toString() {
        return "Gazda{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                ", departament='" + departament + '\'' +
                '}';
    }
}