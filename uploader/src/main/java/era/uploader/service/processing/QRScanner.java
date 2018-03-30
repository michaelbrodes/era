package era.uploader.service.processing;

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
import era.uploader.data.model.QRCodeMapping;
import era.uploader.data.model.QRErrorStatus;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

class QRScanner {

    private MultiFormatReader multiForm;
    private final ScanningProgress progress;
    QRScanner (ScanningProgress progress){
        this.progress = progress;
        multiForm = new MultiFormatReader();
        multiForm.setHints(ImmutableMap.of(
                DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE)
                , DecodeHintType.TRY_HARDER, Boolean.TRUE  //initialize this flag if in later development QR Codes aren't being found in large batches,
                                                           //***required for 300dpi images***
        ));

    }

    String searchForQRCode(PDFRenderer renderer, Map.Entry<Integer,String> idToDocument){
        String tmpFinalResult = null;
        boolean pageFound = false;
        int dpi = 50;
        int maxDPI = 300;

        /*
        * started at 50 dpi, incremented by 1, Found: 113
                                                      122
                                                      127
                                                      144
                                                      98
                                                      115
                                                      127
        * THESE ARE BEST RESULTS SO FAR
        *
        */
        while(!pageFound && dpi < maxDPI){
         try {
             BufferedImage pdf = renderer.renderImageWithDPI(0, dpi);
             BufferedImage topLeft = pdf.getSubimage(0, 0, pdf.getWidth()/2, pdf.getHeight()/2);

             LuminanceSource tmpSource = new BufferedImageLuminanceSource(topLeft);
             BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
             Result tmpResult;

             tmpResult = multiForm.decodeWithState(tmpBitmap);
             tmpFinalResult = String.valueOf(tmpResult.getText());
             if(tmpFinalResult == null){
                 progress.addError(QRErrorStatus.UUID_ERROR.getReason(), idToDocument.getKey().toString());
                 return null;
             } else{
                 pageFound = true;
                 System.out.println(dpi);
             }
         }
         catch (IOException|NotFoundException e){
             //e.printStackTrace();
             dpi += 1;
         }
        }
        return tmpFinalResult;
    }

    QRCodeMapping extractQRCodeInformation(Map.Entry<Integer,String> idToDocument){
        String tmpFinalResult;
        PDDocument document = null;

        try{
            document = PDDocument.load(new File(idToDocument.getValue()));
            PDFRenderer renderer = new PDFRenderer(document);

            BufferedImage pdf = renderer.renderImageWithDPI(0, 300);

            LuminanceSource tmpSource = new BufferedImageLuminanceSource(pdf);
            BinaryBitmap tmpBitmap = new BinaryBitmap(new HybridBinarizer(tmpSource));
            Result tmpResult;
            try{
                tmpResult = multiForm.decodeWithState(tmpBitmap);
                tmpFinalResult = String.valueOf(tmpResult.getText());
            }//inner try
            //if tmpFinalResult is null search harder
            catch (NotFoundException e){
                tmpFinalResult = searchForQRCode(renderer, idToDocument);
            }
            //if tmpFinalResult still null log error and return
            if (tmpFinalResult == null){
                progress.addError(QRErrorStatus.UUID_ERROR.getReason(), idToDocument.getKey().toString());
                return null;
            }
        }//outer try
        catch (IOException e) {
            System.out.println("Error: " + e );
            progress.addError(QRErrorStatus.UUID_ERROR.getReason(), idToDocument.getKey().toString());
            return null;
        }
        finally {
            try{
                if (document != null)
                {
                    document.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        progress.incrementCount();
        return QRCodeMapping.builder()
                .withTempDocumentName(idToDocument.getValue())
                .create(tmpFinalResult);
    }//convertPDFtoBufferedImage
}
