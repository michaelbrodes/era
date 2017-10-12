package era.uploader.processing;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import era.uploader.data.model.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class QRScanner {
    public static Page convertPDFtoBufferedImg(PDDocument document) throws IOException, NotFoundException {
        PDFRenderer renderer = new PDFRenderer(document);
        String tmpFinalResult = "";
        try{
            BufferedImage pdf = renderer.renderImageWithDPI(0, 300);

            LuminanceSource tmpSource = new BufferedImageLuminanceSource(pdf);
            BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
            MultiFormatReader tmpQRcodeReader = new MultiFormatReader();
            Result tmpResult;

            tmpResult = tmpQRcodeReader.decode(tmpBitmap);
            tmpFinalResult = String.valueOf(tmpResult.getText());
            System.out.println(tmpFinalResult);

        }
        catch (IOException | NotFoundException e) {
            System.out.println("Error: " + e );
        }
        return Page.builder()
                .withDocument(document)
                .withUuid(tmpFinalResult)
                .create();
    }//convertPDFtoBufferedImage
}
