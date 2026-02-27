package ro.utcn.ssatr.visitor_system_web.controller;

import ro.utcn.ssatr.visitor_system_web.QRGenerator;
import ro.utcn.ssatr.visitor_system_web.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ro.utcn.ssatr.visitor_system_web.model.Vizita;
import ro.utcn.ssatr.visitor_system_web.repository.VizitaRepository;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/vizite")
public class VizitaController {

    private final VizitaRepository repository;
    private final EmailService emailService;

    public VizitaController(VizitaRepository repository,
                            EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    // ================= LISTA + HEADCOUNT =================
    @GetMapping("")
    public String listaVizite(Model model) {

        List<Vizita> vizite = repository.findAll();
        long headcount = repository.countByStatus("IN_CLADIRE");

        model.addAttribute("vizite", vizite);
        model.addAttribute("headcount", headcount);

        return "vizite";
    }

    // ================= FORMULAR =================
    @GetMapping("/noua")
    public String formularNou(Model model) {
        model.addAttribute("vizita", new Vizita());
        return "formular";
    }

    // ================= SALVARE + QR + EMAIL =================
    @PostMapping("/salveaza")
    public String adaugaVizita(@ModelAttribute Vizita vizita) {

        vizita.setStartTime(LocalDateTime.now());
        vizita.setExpirationTime(LocalDateTime.now().plusHours(2));
        vizita.setStatus("PROGRAMATA");

        repository.save(vizita);

        try {

            String scanUrl =
                    "http://192.168.0.105:8080/vizite/scan/" + vizita.getId();

            String qrFolder = System.getProperty("user.dir")
                    + "/target/classes/static/";

            java.io.File folder = new java.io.File(qrFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String qrPath = qrFolder + "qr_" + vizita.getId() + ".png";

            QRGenerator.generateQRCode(scanUrl, qrPath);

            emailService.sendQrEmail(
                    vizita.getEmail(),
                    scanUrl,
                    qrPath
            );

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/vizite";
    }

    // ================= INTRARE MANUALA =================
    @GetMapping("/intrare/{id}")
    public String intrareVizita(@PathVariable UUID id) {
        Vizita vizita = repository.findById(id).orElseThrow();
        vizita.setStatus("IN_CLADIRE");
        vizita.setEntryTime(LocalDateTime.now());
        repository.save(vizita);
        return "redirect:/vizite";
    }

    // ================= IESIRE MANUALA =================
    @GetMapping("/iesire/{id}")
    public String iesireVizita(@PathVariable UUID id) {
        Vizita vizita = repository.findById(id).orElseThrow();
        vizita.setStatus("IESITA");
        vizita.setExitTime(LocalDateTime.now());
        repository.save(vizita);
        return "redirect:/vizite";
    }

    // ================= ACTIVE =================
    @GetMapping("/active")
    public String viziteActive(Model model) {
        List<Vizita> active = repository.findByStatus("IN_CLADIRE");
        long headcount = repository.countByStatus("IN_CLADIRE");

        model.addAttribute("vizite", active);
        model.addAttribute("headcount", headcount);

        return "vizite";
    }

    // ================= SCAN QR =================
    @GetMapping("/scan/{id}")
    public String scanVizita(@PathVariable UUID id, Model model) {

        Vizita vizita = repository.findById(id).orElseThrow();

        if (vizita.getExpirationTime().isBefore(LocalDateTime.now())) {

            vizita.setStatus("EXPIRATA");
            model.addAttribute("message", "Acces refuzat - QR expirat");
            model.addAttribute("allowed", false);

        } else {

            if ("PROGRAMATA".equals(vizita.getStatus())) {

                // 🔹 INTRARE
                vizita.setStatus("IN_CLADIRE");
                vizita.setEntryTime(LocalDateTime.now());

                model.addAttribute("message", "Intrare înregistrată");
                model.addAttribute("allowed", true);

            } else if ("IN_CLADIRE".equals(vizita.getStatus())) {

                // 🔹 IESIRE
                vizita.setStatus("IESITA");
                vizita.setExitTime(LocalDateTime.now());

                model.addAttribute("message", "Ieșire înregistrată");
                model.addAttribute("allowed", true);

            } else if ("IESITA".equals(vizita.getStatus())) {

                model.addAttribute("message", "Vizita este deja finalizată");
                model.addAttribute("allowed", false);
            }
        }

        repository.save(vizita);
        model.addAttribute("vizita", vizita);

        return "badge";
    }


    // ================= HEADCOUNT API =================
    @GetMapping("/headcount")
    @ResponseBody
    public long headcount() {
        return repository.countByStatus("IN_CLADIRE");
    }

    //===============EVACUARE=======================
    @GetMapping("/evacuare")
    public String evacuare(Model model) {

        List<Vizita> inside = repository.findByStatus("IN_CLADIRE");

        model.addAttribute("vizite", inside);
        model.addAttribute("headcount", inside.size());

        return "evacuare";
    }
    // ================= START EMERGENCY =================
    @GetMapping("/emergency/start")
    public String startEmergency() {

        List<Vizita> inBuilding = repository.findByStatus("IN_CLADIRE");

        for (Vizita v : inBuilding) {
            v.setStatus("IN_EVACUARE");
            repository.save(v);
        }

        return "redirect:/vizite/emergency";
    }

    // ================= EMERGENCY LIST =================
    @GetMapping("/emergency")
    public String emergencyPage(Model model) {

        List<Vizita> evacuare = repository.findByStatus("IN_EVACUARE");
        List<Vizita> evacuati = repository.findByStatus("EVACUAT");

        model.addAttribute("evacuare", evacuare);
        model.addAttribute("evacuati", evacuati);

        return "emergency";
    }

    // ================= CONFIRM EVACUARE =================
    @GetMapping("/emergency/confirm/{id}")
    public String confirmEvacuare(@PathVariable UUID id) {

        Vizita vizita = repository.findById(id).orElseThrow();
        vizita.setStatus("EVACUAT");
        repository.save(vizita);

        return "redirect:/vizite/emergency";
    }
    // ================= MAIL EVACUARE =================
    @GetMapping("/evacuare/trimite")
    public String trimiteEvacuare() {

        List<Vizita> persoane =
                repository.findByStatus("IN_CLADIRE");

        for (Vizita v : persoane) {
            try {
                emailService.sendEvacuationEmail(v.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "redirect:/vizite/evacuare";
    }
}