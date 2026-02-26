package ro.utcn.ssatr.visitor_system_web.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendQrEmail(String to, String scanUrl, String qrPath) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Visitor Access QR Code");

        String text =
                "Bună,\n\n" +
                        "Vizita ta a fost programată.\n\n" +
                        "Scanează QR-ul atașat pentru acces în clădire.\n\n" +
                        "Link direct:\n" + scanUrl + "\n\n" +
                        "Te așteptăm!";

        helper.setText(text);

        // 🔹 verificăm dacă fișierul există înainte să îl atașăm
        File file = new File(qrPath);
        if (file.exists()) {
            FileSystemResource resource = new FileSystemResource(file);
            helper.addAttachment("visitor_qr.png", resource);
        }

        mailSender.send(message);
    }
}