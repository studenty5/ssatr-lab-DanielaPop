package ro.utcn.ssatr.visitor_system_web;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.nio.file.Path;

public class QRGenerator {

    public static void generateQRCode(String text, String filePath) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);
        Path path = Path.of(filePath);
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);
    }
}