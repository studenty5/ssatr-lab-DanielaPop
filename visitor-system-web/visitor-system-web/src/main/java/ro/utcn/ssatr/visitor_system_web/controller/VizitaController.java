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

    // ================= LISTA =================
    @GetMapping("")
    public String listaVizite(Model model) {
        List<Vizita> vizite = repository.findAll();
        model.addAttribute("vizite", vizite);
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
                    "QR Code pentru vizita",
                    "Buna,\n\nAcesta este QR-ul tau pentru acces:\n"
                            + scanUrl
                            + "\n\nPrezinta-l la intrare."
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
        model.addAttribute("vizite", active);
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

            vizita.setStatus("IN_CLADIRE");
            vizita.setEntryTime(LocalDateTime.now());
            model.addAttribute("message", "Acces permis");
            model.addAttribute("allowed", true);
        }

        repository.save(vizita);
        model.addAttribute("vizita", vizita);

        return "badge";
    }

    @GetMapping("/headcount")
    @ResponseBody
    public long headcount() {
        return repository.countByStatus("IN_CLADIRE");
    }
}