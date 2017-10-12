package era.uploader.processing;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import era.uploader.controller.StatusChangeBus;
import era.uploader.data.model.FileStatus;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Converts a PDF that was photo scanned by a TASKalfa scanner into a list of
 * {@link PDDocument} pages. The reason that the file is split up into a list
 * of individual pages is because the scanner just concatenates every scanned
 * page into one huge file. Each page of that huge file corresponds to an
 * existing {@link era.uploader.data.model.Page}. The pages have no predefined
 * order.
 * <p>
 * TASKalfa is the type of photo scanner ERA is written against, however ERA
 * could potentially work with many different types of PDFs provided that they
 * are scanned in as 300 DPI and have a QR code on the top right.
 */
@ParametersAreNonnullByDefault
public class TASKalfaConverter implements Converter<Path, List<PDDocument>> {
    private static final int FIRST_PAGE = 1;
    private static final int PAGES_IN_SPLIT = 1;

    /**
     * Converts a PDF that was photo scanned by a TASKalfa scanner into a List
     * {@link PDDocument} with one document per page in the original PDF.
     *
     * @param file a PDF file that was photo scanned by a TASKalfa scanner
     * @return a list of pages that make up that PDF.
     * @throws IOException PDF doesn't exist.
     */
    @Override
    @Nonnull
    public List<PDDocument> convert(Path file) throws IOException {
        Preconditions.checkNotNull(file);
        Splitter pdfSplitter = new Splitter();
        List<PDDocument> ret;

        try {
            PDDocument inputPDF = PDDocument.load(file.toFile());
            pdfSplitter.setStartPage(FIRST_PAGE);
            pdfSplitter.setEndPage(inputPDF.getNumberOfPages());
            pdfSplitter.setSplitAtPage(PAGES_IN_SPLIT);
            ret = ImmutableList.copyOf(pdfSplitter.split(inputPDF));
        } catch (IOException e) {
            StatusChangeBus
                    .instance()
                    .fire(new StatusChangeEvent(FileStatus.NOT_FOUND));
            throw e;
        }

        return ret;
    }

    @Nonnull
    static List<PDDocument> convertFile(Path file) throws IOException {
        return new TASKalfaConverter().convert(file);
    }
}
