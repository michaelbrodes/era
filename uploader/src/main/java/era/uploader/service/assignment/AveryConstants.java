package era.uploader.service.assignment;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * The set of constants for calculations involving rendering a sheet of Avery
 * labels. There is an inner class for {@link Address} labels and an inner
 * class for {@link Shipping} labels. If the floating point values look weird
 * that is because they were found through guess and check. While we are given
 * the dimensions of the Avery labels, the Avery template doesn't always follow
 * those dimensions.
 *
 * 1in = 72pt. Points are used by PDFBox and ZXing, but inches are used by
 * Avery.
 */
public final class AveryConstants {
    private AveryConstants() {
        // no-op
    }

    public static final class Shipping {
        private Shipping() {
            // no-op
        }

        // page related constants
        /**
         * Points from the top of the page to the first label in the Y direction.
         */
        static final float POINTS_FROM_TOP       = 50.0f;
        /**
         * Points from the left hand side of the page to the first label.
         */
        static final float POINTS_FROM_LEFT_EDGE = 30.0f;
        /**
         * Points from the left hand side cell to the right hand side cell
         */
        static final float POINTS_TO_NEXT_COLUMN = 300.0f;
        /**
         * The amount of points to jump to next row. This would be multiplied by
         * the current row number to get the y coordinate to start the new row.
         */
        static final float POINTS_TO_NEXT_ROW    = 150.0f;
        /**
         * The amount of labels in each row of the sheet. With 2" by 4" shipping
         * labels this is 2.
         */
        static final int   CELLS_PER_ROW         = 2;
        /**
         * Amount of labels in one page.
         */
        static final int   CELLS_PER_PAGE        = 10;

        // QR Code related constants
        /**
         * Height of QR code in label image
         */
        static final int   QR_HEIGHT             = 100;
        /**
         * Width of QR code in label image
         */
        static final int   QR_WIDTH              = 100;
        /**
         * When we are starting a new text section of the QR code this
         * specifies margin from the top of label
         */
        static final float       TEXT_MARGIN_TOP       = 30.0f;
        /**
         * The size of the meta data text on the label
         */
        static final float       FONT_SIZE             = 10.0f;
        /**
         * The line spacing between each line of text on the label.
         */
        static final float       NEW_LINE_OFFSET       = 14.0f;
        /**
         * Points from the QR code to the meta data text.
         */
        static final float       TEXT_MARGIN_LEFT      = 10.0f;

    }

    public static final class Address {
        private Address() {
            // no-op
        }

        /**
         * Amount of labels in one page.
         */
        static final int   CELLS_PER_PAGE       = 30;
        /**
         * The amount of labels in each row of the sheet. With 1" by 2.625"
         * address labels this is 3.
         */
        static final int   CELLS_PER_ROW        = 3;
        /**
         * The amount of points to jump to next row.
         */
        static final float POINTS_TO_NEXT_ROW    = 72.25f;
        /**
         * The amount of points to jump to the next column
         */
        static final float POINTS_TO_NEXT_COLUMN = 205.0f;
        /**
         * Height of the QR code in a 1" by 2.625" address label
         */
        static final int   QR_HEIGHT             = 70;
        /**
         * Width of the QR code in a 1" by 2.625" address label
         */
        static final int   QR_WIDTH              = 70;
        /**
         * Size of text in a 1" by 2.625" address label
         */
        static final float FONT_SIZE             = 8.0f;
        /**
         * The line spacing between each line of text on the label.
         */
        static final float NEW_LINE_OFFSET       = 12.0f;
        /**
         * When we are starting a new text section of the Address label this
         * specifies margin from the top of label
         */
        static final float TEXT_MARGIN_TOP       = 20.0f;
        /**
         * The margin between the top of the page and the header
         */
        static final float HEADER_MARGIN_TOP     = 15.0f;
        /**
         * The margin between the center of the page and the header.
         */
        static final float HEADER_MARGIN_CENTER  = 60.0f;
        /**
         * Points from the top of the page to the first label in the Y direction.
         * I determined this through guess and check, because I don't know where
         * the label starts at.
         */
        static final float POINTS_FROM_TOP       = 36.875f;
        /**
         * Points from the left hand side of the page to the first label.
         */
        static final float POINTS_FROM_LEFT_EDGE = 5.0f;
        /**
         * Points from the QR code to the meta data text.
         */
        static final float TEXT_MARGIN_LEFT      = 0.0f;
    }

    /**
     * The top of font used in the PDF.
     */
    static final PDType1Font FONT             = PDType1Font.HELVETICA;
}
