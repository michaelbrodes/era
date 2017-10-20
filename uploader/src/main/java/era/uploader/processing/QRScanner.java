package era.uploader.processing;

import com.google.common.collect.ImmutableMap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import era.uploader.controller.QRErrorBus;
import era.uploader.creation.QRErrorEvent;
import era.uploader.creation.QRErrorStatus;
import era.uploader.data.model.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

class QRScanner {

    private MultiFormatReader multiForm;

    QRScanner (){
        multiForm = new MultiFormatReader();
        multiForm.setHints(ImmutableMap.of(
                DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE)
                , DecodeHintType.TRY_HARDER, Boolean.TRUE  //initialize this flag if in later development QR Codes aren't being found in large batches,
                                                           //***required for 300dpi images***
        ));

    }

    private static final QRErrorBus BUS = QRErrorBus.instance();

    Page extractQRCodeInformation(String documentName){
        String tmpFinalResult;
        try{
            PDDocument document = PDDocument.load(new File(documentName));
            PDFRenderer renderer = new PDFRenderer(document);

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
            document.close();
        }
        catch (IOException | NotFoundException e) {
            System.out.println("Error: " + e );
            BUS.fire(new QRErrorEvent(QRErrorStatus.UUID_ERROR));
            return null;
        }
        return Page.builder()
                .withTempDocumentName(documentName)
                .create(tmpFinalResult);
    }//convertPDFtoBufferedImage
}
