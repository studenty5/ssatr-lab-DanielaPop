package ro.utcn.ssatr;

import ro.utcn.ssatr.db.DatabaseManager;
import ro.utcn.ssatr.model.*;
import ro.utcn.ssatr.serviciu.ServiciuVizite;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        // 1️⃣ Initializam baza de date
        DatabaseManager.initDatabase();

        ServiciuVizite serviciu = new ServiciuVizite();

        Vizitator vizitator = new Vizitator(
                "Daniela Pop",
                "popdaniela187@email.com",
                TipVizitator.VIZITATOR
        );

        Gazda gazda = new Gazda("Ion Ionescu", "IT");

        // 2️⃣ Cream vizita activa (nu expirata)
        Vizita vizita = new Vizita(
                vizitator,
                gazda,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );

        // 3️⃣ Salvam in sistem + DB
        serviciu.adaugaVizita(vizita);

        // 4️⃣ Generam QR real (PNG)
        vizita.genereazaImagineQr();

        System.out.println("\nVizita programata:");
        System.out.println(vizita);

        // 5️⃣ Simulam scanare intrare
        serviciu.proceseazaIntrare(vizita.getId());

        System.out.println("\nDupa intrare:");
        System.out.println("Persoane in cladire: " +
                serviciu.getNumarPersoaneInCladire());

        // 6️⃣ Simulam iesire
        serviciu.proceseazaIesire(vizita.getId());

        System.out.println("\nDupa iesire:");
        System.out.println("Persoane in cladire: " +
                serviciu.getNumarPersoaneInCladire());
    }
}