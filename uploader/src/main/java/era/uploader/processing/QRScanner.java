package era.uploader.processing;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.sun.org.apache.xpath.internal.operations.Bool;
import era.uploader.controller.QRErrorBus;
import era.uploader.creation.MultimapCollector;
import era.uploader.creation.QRErrorEvent;
import era.uploader.creation.QRErrorStatus;
import era.uploader.data.model.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class QRScanner {

    private MultiFormatReader multiForm;

    public QRScanner (){
        multiForm = new MultiFormatReader();
        multiForm.setHints(ImmutableMap.of(
                DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(BarcodeFormat.QR_CODE)
                , DecodeHintType.TRY_HARDER, Boolean.TRUE  //initialize this flag if in later development QR Codes aren't being found in large batches,
                                                           //***required for 300dpi images***
        ));

    }

    private static final QRErrorBus BUS = QRErrorBus.instance();

    public Page extractQRCodeInformation(PDDocument document){
        PDFRenderer renderer = new PDFRenderer(document);

        String tmpFinalResult = "";
        try{
            BufferedImage pdf = renderer.renderImageWithDPI(0, 300);

            LuminanceSource tmpSource = new BufferedImageLuminanceSource(pdf);
            BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
            Result tmpResult;

            tmpResult = multiForm.decodeWithState(tmpBitmap);
            tmpFinalResult = String.valueOf(tmpResult.getText());
            if(tmpFinalResult == null){
                BUS.fire(new QRErrorEvent(QRErrorStatus.UUID_ERROR));
                return null;
            }
        }
        catch (IOException | NotFoundException e) {
            System.out.println("Error: " + e );
            BUS.fire(new QRErrorEvent(QRErrorStatus.UUID_ERROR));
            return null;
        }
        return Page.builder()
                .withDocument(document)
                .withUuid(tmpFinalResult)
                .create();
    }//convertPDFtoBufferedImage
}
