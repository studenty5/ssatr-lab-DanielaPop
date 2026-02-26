package ro.utcn.ssatr.model;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Reprezinta o vizita programata in cladire.
 */
public class Vizita {

    private UUID id;
    private Vizitator vizitator;
    private Gazda gazda;
    private LocalDateTime dataInceput;
    private LocalDateTime dataSfarsit;
    private StatusVizita status;
    private String codQr;

    public Vizita(Vizitator vizitator,
                  Gazda gazda,
                  LocalDateTime dataInceput,
                  LocalDateTime dataSfarsit) {

        this.id = UUID.randomUUID();
        this.vizitator = vizitator;
        this.gazda = gazda;
        this.dataInceput = dataInceput;
        this.dataSfarsit = dataSfarsit;
        this.status = StatusVizita.PROGRAMATA;
        this.codQr = genereazaCodQr();
    }

    // 🔹 QR va contine informatii complete despre vizita
    private String genereazaCodQr() {

        return "VISITOR_PASS | ID=" + id +
                " | NAME=" + vizitator.getNume() +
                " | HOST=" + gazda.getNume() +
                " | VALID_UNTIL=" + dataSfarsit;
    }

    // 🔹 GENEREAZA IMAGINE QR REALA (PNG)
    public void genereazaImagineQr() {

        try {

            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    codQr,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
            );

            Path path = Path.of("qr_" + id + ".png");

            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

            System.out.println("QR generat: " + path.toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UUID getId() {
        return id;
    }

    public Vizitator getVizitator() {
        return vizitator;
    }

    public Gazda getGazda() {
        return gazda;
    }

    public LocalDateTime getDataInceput() {
        return dataInceput;
    }

    public LocalDateTime getDataSfarsit() {
        return dataSfarsit;
    }

    public StatusVizita getStatus() {
        return status;
    }

    public String getCodQr() {
        return codQr;
    }

    public void marcheazaIntrare() {
        this.status = StatusVizita.IN_CLADIRE;
    }

    public void marcheazaIesire() {
        this.status = StatusVizita.FINALIZATA;
    }

    public void verificaExpirare() {

        if (LocalDateTime.now().isAfter(dataSfarsit)
                && status != StatusVizita.FINALIZATA) {

            this.status = StatusVizita.EXPIRATA;
        }
    }

    @Override
    public String toString() {
        return "Vizita{" +
                "id=" + id +
                ", vizitator=" + vizitator.getNume() +
                ", gazda=" + gazda.getNume() +
                ", status=" + status +
                '}';
    }
}