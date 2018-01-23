package era.uploader.processing;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import era.uploader.data.model.QRCodeMapping;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Converts a PDF that was photo scanned by a TASKalfa scanner into a list of
 * {@link PDDocument} pages. The reason that the file is split up into a list
 * of individual pages is because the scanner just concatenates every scanned
 * page into one huge file. Each page of that huge file corresponds to an
 * existing {@link QRCodeMapping}. The pages have no predefined
 * order.
 * <p>
 * TASKalfa is the type of photo scanner ERA is written against, however ERA
 * could potentially work with many different types of PDFs provided that they
 * are scanned in as 300 DPI and have a QR code on the top right.
 */
@ParametersAreNonnullByDefault
public class PDFExploder {
    private static final int FIRST_PAGE = 1;
    private static final int PAGES_IN_SPLIT = 1;

    @Nonnull
    private Map<Integer, String> explode(PDDocument inputPDF) throws IOException {
        Preconditions.checkNotNull(inputPDF);
        Splitter pdfSplitter = new Splitter();
        Map<Integer, String> mappedFiles = new LinkedHashMap<>();

        pdfSplitter.setStartPage(FIRST_PAGE);
        pdfSplitter.setEndPage(inputPDF.getNumberOfPages());
        pdfSplitter.setSplitAtPage(PAGES_IN_SPLIT);

        ImmutableList.Builder<String> tmpFiles = ImmutableList.builder();
        Random rand = new Random();

        for (PDDocument document: pdfSplitter.split(inputPDF)) {
            StringBuilder filenameBuilder = new StringBuilder();
            while (filenameBuilder.length() < 10) {
                filenameBuilder.append(rand.nextInt());
            }
            String filename = filenameBuilder.toString();
            File pdf = File.createTempFile(filename, ".pdf");
            document.save(pdf);
            tmpFiles.add(pdf.getAbsolutePath());
            document.close();
        }

        List<String> fileNames = tmpFiles.build();
        for (int i = 1; i <= fileNames.size(); i++){
                mappedFiles.put(i, fileNames.get(i-1));
        }

        inputPDF.close();

        return mappedFiles;
    }

    /**
     * Takes in a multi-page PDF file, explodes the pdf files into individual
     * pages, saves each page to a temporary directory, then returns a Map
     * of the page number of the page to its temporary location.
     *
     * @param inputPDF a mult-page pdf file
     * @return a map containing entries for each page. Each entry will have
     * the page's page number as the key, and the page's temporary location
     * as value
     * @throws IOException PDF doesn't exist.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Nonnull
    static Map<Integer, String> explodeFile(Optional<PDDocument> inputPDF) throws IOException {
        PDFExploder exploder = new PDFExploder();
        Map<Integer, String> idToDocument = ImmutableMap.of();
        if (inputPDF.isPresent()) {
            PDDocument pdDocument = inputPDF.get();
            idToDocument = exploder.explode(pdDocument);
        }
        return idToDocument;
    }
}
